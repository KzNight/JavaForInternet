import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Kz_Night ��������Ҫ���ڡ���ִ�� �ȿ�360ҳ����
 *
 */
public class EchoServer {
	private static Selector selector;// ȫ���Ա��ڲ�ͬ��������

	public static void main(String[] args) throws Exception {
		new EchoServer().service();
	}

	public EchoServer() throws Exception {
		// ��0��:open selector��ServerSocketChannel��������
		selector = java.nio.channels.Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();// ע������Ĵ�������
		serverChannel.configureBlocking(false);
		serverChannel.bind(new InetSocketAddress(6666));

		// ��1��:serversocketί��selector��ע���¼������ĵ��¼�
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("server start!");
	}

	public void service() throws Exception {

		/**
		 * ���������������ͨ�ÿ�ܣ�reactor��multi����·�������� while(true){
		 * if(selector.select()>0){//�ȴ�ֱ���о����¼� //��������¼��ѷ�����selection���� Set<SelectionKey>
		 * readkeys = selector.selectedKeys(); Iterator<SelectionKey> iterator =
		 * readkeys,iterator();//���������� while(iterator.hasNext()){ SelectionKey key =
		 * iterator.next(); iterator.remove(); //���¿���������׼�����ˣ��������Ӿ����¼����������¼���д�����¼�
		 * if(key.isAcceptable()){ //���ܿͻ����������� } if(key.isReadable()){ //�ӿͻ��˶� }
		 * if(key.isWritable()){ //��ͻ���д } } } }
		 *
		 * //ʵ�ֽ������ӡ�����д�ĺ���
		 */
		// ��2��:�����������ȴ�
		while (true) {
			if (selector.select() > 0) {// �ȴ�ֱ���о����¼�
				// ��������¼��ѷ�����selectionkey����
				Set<SelectionKey> readkeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = readkeys.iterator();// ����������
				while (iterator.hasNext()) {// ��3��:���ݵȴ����¼��ֿ�handler
					SelectionKey key = iterator.next();// keyһ�絣����
					iterator.remove();
					// ���¿���������׼�����ˣ��������Ӿ����¼����������¼���д�����¼�
					if (key.isAcceptable()) {
						// ���ܿͻ�����������
						myAccept(key);
					}
					if (key.isReadable()) {
						// �ӿͻ��˶������Դ�ĳ���ͻ��˶�������
						myRead(key);
					}
					if (key.isWritable()) {
						// ��ͻ���д��������ĳ���ͻ���д������
						myWrite(key);
					}
				}
			}
		}
	}

//	SelectableChannel��Selectorע���¼��Ĺ����лᴴ��һ��SelectionKey
//	1)serverSelectable��Selectorע���¼�,����һ��SelectionKey
//	2)Selectable��Selectorע���¼�,����һ��SelectionKey
	private static void myAccept(SelectionKey key) throws Exception {// ���Ӿ���
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		// �������й̶�
		System.out.println("accpet client:" + client.getRemoteAddress());
		client.configureBlocking(false);// true����ģʽ��false������ģʽ

		SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);// ע�����ڱ��ͻ��˵�key

		// ���˶��٣����˶��٣���Ҫ���ֳ���¼�����á�BUFFER����¼
		ByteBuffer buffer = java.nio.ByteBuffer.allocate(1024);// һ��һ�㲻��1024
		clientKey.attach(buffer);// ��ʱClientkey����һ�絣Socketchannel��selector��������һ��BUFFER��Ϊ���߸���
	}

	private static void myRead(SelectionKey key) throws Exception {// ������
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		// �������й̶�
		client.read(buffer);// ÿ�ν������¶����ܶ����ٶ�����
	}

	private static void myWrite(SelectionKey key) throws Exception {// д����
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		
		ByteBuffer echoBuffer = buffer.duplicate();
		echoBuffer.rewind();
		//���echoBuffer�Ƿ����'\n'��Ҳ���Ǽ��buffer��Ŀǰ�յ����������Ƿ�һ��
		while(echoBuffer.hasRemaining() && echoBuffer.get() != '\n'); 
		if(echoBuffer.position() <= buffer.position()) {//�����һ��
			//��һ������ԭ�ⲻ���ط������ͻ���
			echoBuffer.flip();
			while(echoBuffer.hasRemaining()) {
				client.write(echoBuffer);
			}
			//��buffer��ɾ����һ������
			buffer.flip();
			buffer.position(echoBuffer.limit());
			buffer.compact();
		}
	}
}
