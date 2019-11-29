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
 * @date 2019��11��28�� ǿ��������ʵ�֣���Ҫʹ��selectableChannel��selectorע���¼��ķ���ʵ��
 */
public class MissonFourPart01 {
	static int BLOCK = 10240;// 10MB
	private static Selector selector;// ������һ��selector������ʵ�����Ա���ȫ�֣��Լ�ȥ���ʼ��ϵ�ͼ��
	// ��������ڿε�һ��С�ص㣬Ϊ��ֻ��һ�δ��ļ�������дһ���ڲ��࣬ȫ������һ�����������ļ���bigbuffer
	// �Թ������ͻ�����accpet�¼��и������ȫ��bigbuffer��Ȼ����Ӹ��Ƶ�clientbufferΪ����������ÿ���ͻ��˵�write�¼��н�����clientbuffer��ȡ����
	// �����ļ��Ƚϴ�ÿ�α���ֻ��������Լ���clientbuffer�е�һС���֣�Ȼ���������write�¼���Ȼ��ȴ���һ��wirte�¼����������ж��ڱ��ͻ����Լ���clientbuffer�����
	// ����ʹ�������Լ����Ǹ�clientbuffer����˼�¼�ֳ��Ĺ��ܣ����ҷ�ֹ���̳߳�������æµ״̬
	static ByteBuffer bigBuffer;// ֻ����һ���ļ�
//	protected static class HandlerBuffer {// ����������ᵽ���ڲ���
//		protected FileChannel fileChannel;// �ļ�ͨ�����ο���https://www.cnblogs.com/zhya/p/9640016.html
//		protected Buffer directBuffer;// buffer���壬�ο���https://blog.csdn.net/xialong_927/article/details/81044759
//
//		@SuppressWarnings("resource") // eclipse�ŵò��У�����û��channel�����������ʾ��֪����û�أ�eclipse�Ͳ�����
//
//		public HandlerBuffer() throws Exception {// �ڲ���Ĺ��췽��
//			this.fileChannel = new FileInputStream("flightdata.txt").getChannel();// Ϊͨ������ļ�
//			this.directBuffer = ByteBuffer.allocateDirect(10240); // ������JVM��ջ�����ǲ���ϵͳ�������ڴ����Ϊ����������С10MB���뵱ǰ����ϵͳ�ܹ����õ���ϣ�����ܽ�һ�����I/O�����ٶ�
//		}
//
//		public Buffer readBlock() {// �����ļ����� ���ɹ�����һ��buffer
//			try {
//				directBuffer.clear();
//				int flag = fileChannel.read((ByteBuffer) directBuffer);// ���ļ��ɹ���
//				directBuffer.flip();// ��BUFFER��״̬����Ϊ׼��ȡ��״̬
//				if (flag <= 0)// ȡʧ����
//					return null;
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			return directBuffer;
//		}
//
//		public void close() { // �ر�ͨ��
//			try {
//				fileChannel.close(); // ע��ص�ʱ�䣬ֻ��û���κοͻ�������ʱ�Ź�
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

//	Buffer bigBuffer = new HandlerBuffer().readBlock();// ȫ��ֻ����һ���ļ���ÿ���ͻ��˵�buffer���������,��Ϊ��̬

	public static void main(String[] args) throws Exception {
		// �����������ͺ�
		new MissonFourPart01().service();
		
	}

	@SuppressWarnings("static-access") // eclipse�ŵò��У�����û��selector�����������ʾ��֪����û�أ�eclipse�Ͳ�����
	public MissonFourPart01() throws Exception {// ʵ�ַ�������socketͨ�ţ�����selectorע���¼�
		// ��0�� ��selecto��serversocketchannel��������Ϊ������
		selector = selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);// ��������
		serverSocketChannel.bind(new InetSocketAddress(1818));
		// ��1�� ע���¼��������ע��accpet���Ƴٶ��ڶ�дʱ��Ĺ�ע
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("����������! ");
		Path file = FileSystems.getDefault().getPath("flightdata.txt");
		byte[] data = Files.readAllBytes(file);
		bigBuffer =ByteBuffer.wrap(data);
	}

	public void service() throws Exception {// ʵ�ַ������Ĺ��ܣ���Ϊ����ʵ��ֻ�Ƿ����ݣ���������д�¼�
		while (true) {// ��3�� ��������������
			if (selector.select() > 0) {
				Set<SelectionKey> readkeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = readkeys.iterator();// ����������
				while (iterator.hasNext()) {// ��3��:���ݵȴ����¼��ֿ�handler
					SelectionKey key = iterator.next();// ���ڴ˿ͻ��˵�keyһ�絣����
					iterator.remove();
					// ���¿���������׼�����ˣ��������Ӿ����¼����������¼���д�����¼�
					if (key.isAcceptable()) {
						// ���ܿͻ�����������
						myAccept(key);
					}
					if (key.isReadable()) {// �߸���ʽ�����ʵ��û���ⲿ��
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
//	1)serverSocketChannel��Selectorע���¼�,����һ��SelectionKey��Ϊkey0
//	2)serverSocketChannel���صģ���accept������socketchannel��Selector�ٴ�ע���¼�,����һ��SelectionKey��Ϊkey1
//	3)key0��key1������ͬһ���ͻ��ˣ���ȷ�����Ӻ󣬴�����key1ʹ��ע���¼�������read��write
//	������һ�����⣬key1��key0��ô�����ע�¼��ģ�����һ����serversocketchannel�����ģ�һ����socketchannel�����ģ�������һ����
//	�𰸣��Լ�ȥ��������true��ѭ�����˼ҵľ����¼������Ǵ�selector��ȡ�ģ�key1��key0��Ȼ�����߲�ͬ������������������selector���桤һ�絣���£�����ֻ��һ��������̫��������ע���¼���������Ϊɶ����ֻ��һ��̫����������̫��ע���¼�����ΪΨ���������°ף���
	private void myAccept(SelectionKey key) throws Exception {// ���Ӿ���
		ServerSocketChannel server = (ServerSocketChannel) key.channel();// ��key0��ȡ̫��
		SocketChannel client = server.accept();// ��ȡ������socketchannel
		// �������й̶�
		System.out.println("���ܿͻ��ˣ�" + client.getRemoteAddress());
		client.configureBlocking(false);// true����ģʽ��false������ģʽ

		SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);// ע�����ڱ��ͻ��˵�key����ʵ��ֻ����write

		// ���˶��٣����˶��٣���Ҫ���ֳ���¼�����á�BUFFER����¼
		Buffer clientbuffer = bigBuffer.duplicate();//���︴����һ�ݣ���Ϊ����ͷ������buffer
		while (clientbuffer.hasRemaining() && ((ByteBuffer) clientbuffer).get() != '\n');
		clientKey.attach(clientbuffer);// ��ʱClientkey����һ�絣Socketchannel��selector��������һ��BUFFER��Ϊ���߸���
	}

	private void myRead(SelectionKey key) throws Exception {
		// nothinghere
	}

	private void myWrite(SelectionKey key) throws Exception {
//		����һ��ֵ��ע��������ǣ�����ֻʹ����һ���̣߳�����߳�Ҳ��ˣ������û���������Ҫ�����̱߳�����������������¼��Ĵ����߱���Ҫ���̷��أ���������ѭ���У������Ӱ�������û��������ٶ�
//		���嵽�������У������ļ��Ƚϴ����һ���Է��������ļ��������һ���Բ���ָsend�����ļ����ݣ�����ͨ��whileѭ������ϵķ��ͷ��� �����������߳̾ͻ������������û��Ͳ�����Ӧ�ˡ�����Ľ�������ǵ���WRITE�¼�ʱ�������Ƿ���һ���飨����4K�ֽڣ�������󣬼����ȴ�WRITE �¼����֣����δ���ֱ�������ļ�������ϣ������Ͳ������������û��ˡ�
//		��ÿ���ͻ��˵�write�¼��н�����clientbuffer��ȡ�����������ļ��Ƚϴ�ÿ�α���ֻ��������Լ���clientbuffer�е�һС���֣�Ȼ���������write�¼���Ȼ��ȴ���һ��wirte�¼����������ж��ڱ��ͻ����Լ���clientbuffer�����������ʹ�������Լ����Ǹ�clientbuffer����˼�¼�ֳ��Ĺ��ܣ����ҷ�ֹ���̳߳�������æµ״̬
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		// �������й̶�
		ByteBuffer clientbuffer = buffer.duplicate();//�����ָ�����һ�ݣ���Ϊ��ʱbuffer
		clientbuffer.rewind();
		// ���echoBuffer�Ƿ����'\n'��Ҳ���Ǽ��buffer��Ŀǰ�յ����������Ƿ�һ��
		while (clientbuffer.hasRemaining() && clientbuffer.get() != '\n');
		if (clientbuffer.position() <= buffer.position()) {// �����һ��
			// ��һ������ԭ�ⲻ���ط������ͻ���
			clientbuffer.flip();
			while (clientbuffer.hasRemaining()) {
				client.write(clientbuffer);
				Thread.sleep(100);
			}
			// ��buffer��ɾ����һ������
			buffer.compact();
			buffer.limit(buffer.position());
			buffer.position(0);
			while (buffer.hasRemaining() && buffer.get() != '\n');
		}

	}

}
