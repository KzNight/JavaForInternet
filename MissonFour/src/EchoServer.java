import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Kz_Night 非阻塞主要在于“破执” 先看360页例子
 *
 */
public class EchoServer {
	private static Selector selector;// 全局以便在不同函数利用

	public static void main(String[] args) throws Exception {
		new EchoServer().service();
	}

	public EchoServer() throws Exception {
		// 第0步:open selector和ServerSocketChannel，并配置
		selector = java.nio.channels.Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();// 注意这里的创建方法
		serverChannel.configureBlocking(false);
		serverChannel.bind(new InetSocketAddress(6666));

		// 第1步:serversocket委托selector（注册事件）关心的事件
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("server start!");
	}

	public void service() throws Exception {

		/**
		 * 非阻塞服务器编程通用框架，reactor，multi（多路复用器） while(true){
		 * if(selector.select()>0){//等待直到有就绪事件 //或许相关事件已发生的selection集合 Set<SelectionKey>
		 * readkeys = selector.selectedKeys(); Iterator<SelectionKey> iterator =
		 * readkeys,iterator();//迭代器迭代 while(iterator.hasNext()){ SelectionKey key =
		 * iterator.next(); iterator.remove(); //以下看究竟哪种准备好了：接受链接就绪事件、读就绪事件、写就绪事件
		 * if(key.isAcceptable()){ //接受客户端连接请求 } if(key.isReadable()){ //从客户端读 }
		 * if(key.isWritable()){ //向客户端写 } } } }
		 *
		 * //实现接受连接、读、写的函数
		 */
		// 第2步:服务器持续等待
		while (true) {
			if (selector.select() > 0) {// 等待直到有就绪事件
				// 或许相关事件已发生的selectionkey集合
				Set<SelectionKey> readkeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = readkeys.iterator();// 迭代器迭代
				while (iterator.hasNext()) {// 第3步:根据等待的事件分开handler
					SelectionKey key = iterator.next();// key一肩担日月
					iterator.remove();
					// 以下看究竟哪种准备好了：接受链接就绪事件、读就绪事件、写就绪事件
					if (key.isAcceptable()) {
						// 接受客户端连接请求
						myAccept(key);
					}
					if (key.isReadable()) {
						// 从客户端读，可以从某个客户端读数据了
						myRead(key);
					}
					if (key.isWritable()) {
						// 向客户端写，可以向某个客户端写数据了
						myWrite(key);
					}
				}
			}
		}
	}

//	SelectableChannel向Selector注册事件的过程中会创建一个SelectionKey
//	1)serverSelectable向Selector注册事件,创建一个SelectionKey
//	2)Selectable向Selector注册事件,创建一个SelectionKey
	private static void myAccept(SelectionKey key) throws Exception {// 连接就绪
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		// 以上两行固定
		System.out.println("accpet client:" + client.getRemoteAddress());
		client.configureBlocking(false);// true阻塞模式，false非阻塞模式

		SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);// 注册属于本客户端的key

		// 收了多少，发了多少，需要“现场记录”。用【BUFFER】记录
		ByteBuffer buffer = java.nio.ByteBuffer.allocate(1024);// 一行一般不超1024
		clientKey.attach(buffer);// 此时Clientkey不仅一肩担Socketchannel和selector，还带了一个BUFFER作为工具附件
	}

	private static void myRead(SelectionKey key) throws Exception {// 读就绪
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		// 以上两行固定
		client.read(buffer);// 每次接着往下读，能读多少读多少
	}

	private static void myWrite(SelectionKey key) throws Exception {// 写就绪
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		
		ByteBuffer echoBuffer = buffer.duplicate();
		echoBuffer.rewind();
		//检查echoBuffer是否包含'\n'，也就是检查buffer里目前收到的数据里是否够一行
		while(echoBuffer.hasRemaining() && echoBuffer.get() != '\n'); 
		if(echoBuffer.position() <= buffer.position()) {//如果够一行
			//第一行数据原封不动地发还给客户端
			echoBuffer.flip();
			while(echoBuffer.hasRemaining()) {
				client.write(echoBuffer);
			}
			//从buffer里删除第一行数据
			buffer.flip();
			buffer.position(echoBuffer.limit());
			buffer.compact();
		}
	}
}
