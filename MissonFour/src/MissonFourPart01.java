import java.awt.desktop.ScreenSleepEvent;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Kz_Night
 * @date 2019年11月28日 强调非阻塞实现，需要使用selectableChannel向selector注册事件的方法实现
 */
public class MissonFourPart01 {
	static int BLOCK = 10240;// 10MB
	private static Selector selector;// 先生成一个selector监视器实例，以便监控全局（自己去看笔记上的图）
	// 以下是这节课的一个小重点，为了只读一次大文件，单独写一个内部类，全局生成一个完整读完文件的bigbuffer
	// 以供各个客户端在accpet事件中复制这个全局bigbuffer，然后添加复制的clientbuffer为附件，并在每个客户端的write事件中将附件clientbuffer获取出来
	// 由于文件比较大，每次必须只输出属于自己的clientbuffer中的一小部分，然后结束本次write事件，然后等待下一次wirte事件，继续进行对于本客户端自己的clientbuffer的输出
	// 即：使用属于自己的那个clientbuffer完成了记录现场的功能，并且防止了线程持续处于忙碌状态
	static ByteBuffer bigBuffer;// 只读这一次文件
//	protected static class HandlerBuffer {// 这就是上面提到的内部类
//		protected FileChannel fileChannel;// 文件通道，参考：https://www.cnblogs.com/zhya/p/9640016.html
//		protected Buffer directBuffer;// buffer缓冲，参考：https://blog.csdn.net/xialong_927/article/details/81044759
//
//		@SuppressWarnings("resource") // eclipse慌得不行，怕我没关channel，加上这个表示我知道我没关，eclipse就不慌了
//
//		public HandlerBuffer() throws Exception {// 内部类的构造方法
//			this.fileChannel = new FileInputStream("flightdata.txt").getChannel();// 为通道添加文件
//			this.directBuffer = ByteBuffer.allocateDirect(10240); // 不是用JVM堆栈，而是操作系统来创建内存块作为缓冲区，大小10MB，与当前操作系统能够更好的耦合，因此能进一步提高I/O操作速度
//		}
//
//		public Buffer readBlock() {// 做读文件操作 若成功返回一个buffer
//			try {
//				directBuffer.clear();
//				int flag = fileChannel.read((ByteBuffer) directBuffer);// 读文件成功否？
//				directBuffer.flip();// 将BUFFER的状态调整为准备取的状态
//				if (flag <= 0)// 取失败了
//					return null;
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			return directBuffer;
//		}
//
//		public void close() { // 关闭通道
//			try {
//				fileChannel.close(); // 注意关的时间，只在没有任何客户端需求时才关
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

//	Buffer bigBuffer = new HandlerBuffer().readBlock();// 全局只读这一次文件，每个客户端的buffer从这个复制,设为静态

	public static void main(String[] args) throws Exception {
		// 启动服务器就好
		new MissonFourPart01().service();
		
	}

	@SuppressWarnings("static-access") // eclipse慌得不行，怕我没关selector，加上这个表示我知道我没关，eclipse就不慌了
	public MissonFourPart01() throws Exception {// 实现服务器的socket通信，并向selector注册事件
		// 第0步 打开selecto和serversocketchannel，并配置为非阻塞
		selector = selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);// 非阻塞、
		serverSocketChannel.bind(new InetSocketAddress(1818));
		// 第1步 注册事件，这里仅注册accpet，推迟对于读写时间的关注
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("服务器开启! ");
		Path file = FileSystems.getDefault().getPath("flightdata.txt");
		byte[] data = Files.readAllBytes(file);
		bigBuffer =ByteBuffer.wrap(data);
	}

	public void service() throws Exception {// 实现服务器的功能，以为本次实验只是发数据，所以重在写事件
		while (true) {// 第3步 服务器持续待机
			if (selector.select() > 0) {
				Set<SelectionKey> readkeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = readkeys.iterator();// 迭代器迭代
				while (iterator.hasNext()) {// 第3步:根据等待的事件分开handler
					SelectionKey key = iterator.next();// 属于此客户端的key一肩担日月
					iterator.remove();
					// 以下看究竟哪种准备好了：接受链接就绪事件、读就绪事件、写就绪事件
					if (key.isAcceptable()) {
						// 接受客户端连接请求
						myAccept(key);
					}
					if (key.isReadable()) {// 走个形式，这个实验没有这部分
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
//	1)serverSocketChannel向Selector注册事件,创建一个SelectionKey，为key0
//	2)serverSocketChannel返回的（用accept方法）socketchannel向Selector再次注册事件,创建一个SelectionKey，为key1
//	3)key0，key1服务于同一个客户端，在确定连接后，创建的key1使关注的事件增加了read和write
//	这里有一个问题，key1和key0怎么共享关注事件的？他俩一个是serversocketchannel建立的，一个是socketchannel建立的，他俩不一样。
//	答案：自己去看服务器true的循环，人家的就绪事件队列是从selector获取的，key1和key0虽然创建者不同，但是他俩都役属于selector。真·一肩担日月，而且只有一个月亮，太阳向月亮注册事件（别问我为啥不是只有一个太阳、月亮向太阳注册事件，因为唯见江心秋月白）。
	private void myAccept(SelectionKey key) throws Exception {// 连接就绪
		ServerSocketChannel server = (ServerSocketChannel) key.channel();// 从key0获取太阳
		SocketChannel client = server.accept();// 获取建立的socketchannel
		// 以上两行固定
		System.out.println("接受客户端：" + client.getRemoteAddress());
		client.configureBlocking(false);// true阻塞模式，false非阻塞模式

		SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);// 注册属于本客户端的key，本实验只关心write

		// 收了多少，发了多少，需要“现场记录”。用【BUFFER】记录
		Buffer clientbuffer = bigBuffer.duplicate();//这里复制了一份，作为这个客服独享的buffer
		while (clientbuffer.hasRemaining() && ((ByteBuffer) clientbuffer).get() != '\n');
		clientKey.attach(clientbuffer);// 此时Clientkey不仅一肩担Socketchannel和selector，还带了一个BUFFER作为工具附件
	}

	private void myRead(SelectionKey key) throws Exception {
		// nothinghere
	}

	private void myWrite(SelectionKey key) throws Exception {
//		另外一个值得注意的问题是：由于只使用了一个线程（多个线程也如此）处理用户请求，所以要避免线程被阻塞，解决方法是事件的处理者必须要即刻返回，不能陷入循环中，否则会影响其他用户的请求速度
//		具体到本例子中，由于文件比较大，如果一次性发送整个文件（这里的一次性不是指send整个文件内容，而是通过while循环不间断的发送分组 包），则主线程就会阻塞，其他用户就不能响应了。这里的解决方法是当有WRITE事件时，仅仅是发送一个块（比如4K字节）。发完后，继续等待WRITE 事件出现，依次处理，直到整个文件发送完毕，这样就不会阻塞其他用户了。
//		在每个客户端的write事件中将附件clientbuffer获取出来，由于文件比较大，每次必须只输出属于自己的clientbuffer中的一小部分，然后结束本次write事件，然后等待下一次wirte事件，继续进行对于本客户端自己的clientbuffer的输出。即：使用属于自己的那个clientbuffer完成了记录现场的功能，并且防止了线程持续处于忙碌状态
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		// 以上两行固定
		ByteBuffer clientbuffer = buffer.duplicate();//这里又复制了一份，作为临时buffer
		clientbuffer.rewind();
		// 检查echoBuffer是否包含'\n'，也就是检查buffer里目前收到的数据里是否够一行
		while (clientbuffer.hasRemaining() && clientbuffer.get() != '\n');
		if (clientbuffer.position() <= buffer.position()) {// 如果够一行
			// 第一行数据原封不动地发还给客户端
			clientbuffer.flip();
			while (clientbuffer.hasRemaining()) {
				client.write(clientbuffer);
				Thread.sleep(100);
			}
			// 从buffer里删除第一行数据
			buffer.compact();
			buffer.limit(buffer.position());
			buffer.position(0);
			while (buffer.hasRemaining() && buffer.get() != '\n');
		}

	}

}
