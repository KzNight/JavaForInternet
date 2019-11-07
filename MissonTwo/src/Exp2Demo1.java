import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Kz_Night
 *	������������/����������
 *	��������������������
 */
public class Exp2Demo1 {
	private static LinkedList<String> entries = new LinkedList<String>();
	private static Thread consumerThread;// ����������

	public static void main(String[] args) {
		// ��������������������
//		new Thread(new Runnable() {
//			public void run() {
//				processEntry();
//			}
//		}).start();
		// ����
//		new Thread(()->{processEntry();}).start();
		consumerThread = new Thread(() -> {
			processEntry();
		});
		consumerThread.start();

		// ��������������������
//		new Thread(new Runnable() {
//			public void run() {
//				readDataFile();();
//			}
//		}).start();
		new Thread(() -> {
			readDataFile();
		}).start();

	}

	private static void processEntry() {// ������������������������
		while (true) {
			synchronized (entries) {
				while (entries.isEmpty()) {// ��������if������wait��������������������������������������������
					try {
						entries.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						return;
					}
				}
				// !!!��������������while����������������while��������������������������wait������������������������������������������������
				String entry = entries.remove(entries.size() - 1);
				System.out.println(entry);
			}
		}
	}

	private static void readDataFile() {
		try (Scanner scanner = new Scanner(new File("fdsdata.txt"))) {
			while (true) {
				// ����������
//			Path file = Paths.get("fdsdata.txt");//paths������������������

				if (!scanner.hasNextLine()) {
					consumerThread.interrupt();// ��������������������������������������
					break;
				} 
				String entry = scanner.nextLine();
				synchronized (entries) {
				entries.add(0, entry);
				entries.notifyAll();// ��������������all					}
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
