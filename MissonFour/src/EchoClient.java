import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Kz_Night
 *从服务器读的客户端不用考虑并发
 */
public class EchoClient {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try(Socket socket = new Socket("localhost", 6666);
				Scanner socketScanner = new Scanner(socket.getInputStream());
				PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
				Scanner localScanner = new Scanner(System.in)){
			
					String line = localScanner.nextLine();//从键盘读
					while(!line.equals("quit")) {
						pw.println(line);//交给服务器
						System.out.println(socketScanner.nextLine());//从服务器获取
						line = localScanner.nextLine();//再次获取键盘
					}
				}
	}

}
