import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		PrintStream outfile = new PrintStream("./���۶�̬��ʾ.txt");
		System.setOut(outfile);//�ı��ļ������Ϊ�ļ���ע�͵�������ʾ��console��
		try(Socket socket = new Socket("localhost",1818);//�Լ�д�ķ����� �Լ�֪���ĸ��˿ڣ���netstat -an��û���õ�
				Scanner scanner = new Scanner(socket.getInputStream())){
			while(scanner.hasNextLine()) {
				System.out.println(scanner.nextLine());
			}
		}
	}

}
