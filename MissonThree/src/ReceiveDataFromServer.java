import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ReceiveDataFromServer {

	/**
	 * 2019.11.17 ���ϴ���demo
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try (Socket socket = new Socket("10.5.25.193", 9999); 
				Scanner scanner = new Scanner(socket.getInputStream())) {
			//��ʼ�ӷ����������ݣ�ע��������Լ����������ӳ�
			String line = null;
			while(scanner.hasNextLine()) {
				line = scanner.nextLine();
				System.out.println(line);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
