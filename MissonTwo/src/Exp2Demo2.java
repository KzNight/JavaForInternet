import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Kz_Night
 *	ʵ�鱨��ʹ�õ�һ�ַ��������Ժ������ʹ�þ����Ż���LinkedBlockQueue
 *	����api�ĵ�BlockQueue�ı�
 */
public class Exp2Demo2 {
	public static void main(String[] args) {
		LinkedBlockingQueue<String> entries = new LinkedBlockingQueue<String>();
		
		new Thread(()->{
			Path file = Paths.get("fdsdata.txt");
			try {
				Files.lines(file).forEach(line->{
					try {
						entries.put(line);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				entries.put("no data");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}).start();
		
		new Thread(()->{
			try {
				while(true) {
				String line =entries.take();
				if(entries.equals("no data")) break;
				System.out.println(line);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start(); 
	}
}
