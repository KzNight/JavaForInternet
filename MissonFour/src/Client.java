import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		PrintStream outfile = new PrintStream("./进港动态显示.txt");
		System.setOut(outfile);//改变文件输出流为文件，注释掉就是显示在console中
		try(Socket socket = new Socket("localhost",1818);//自己写的服务器 自己知道哪个端口，用netstat -an查没被用的
				Scanner scanner = new Scanner(socket.getInputStream())){
			while(scanner.hasNextLine()) {
				System.out.println(scanner.nextLine());
			}
		}
	}

}
