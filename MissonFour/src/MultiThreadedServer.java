import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Executable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class MultiThreadedServer {
	public static void main(String[] args) throws Exception{
		ServerSocket serverSocket = new ServerSocket(8888);
		System.out.println("Server start");
		ExecutorService pool = Executors.newFixedThreadPool(16);
		while(true){
			Socket socket = serverSocket.accept();//不放在try里，因为等会儿会放进线程池
			System.out.println("get a new client,ip adress:"+socket.getInetAddress()+" port:"+socket.getPort());
			Handler hander = new Handler(socket);
			pool.execute(hander);
		}
	}
}
class Handler implements Runnable {
	private Socket socket;

	public Handler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try(Scanner scanner = new Scanner(new FileInputStream("flightdata.txt"));
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)){
			pw.println("一共有18018行数据");
			while(scanner.hasNextLine()) {
				pw.println(scanner.nextLine());
				Thread.sleep(10);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
