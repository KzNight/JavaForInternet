import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Kz_Night
 *�ӷ��������Ŀͻ��˲��ÿ��ǲ���
 */
public class EchoClient {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try(Socket socket = new Socket("localhost", 6666);
				Scanner socketScanner = new Scanner(socket.getInputStream());
				PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
				Scanner localScanner = new Scanner(System.in)){
			
					String line = localScanner.nextLine();//�Ӽ��̶�
					while(!line.equals("quit")) {
						pw.println(line);//����������
						System.out.println(socketScanner.nextLine());//�ӷ�������ȡ
						line = localScanner.nextLine();//�ٴλ�ȡ����
					}
				}
	}

}
