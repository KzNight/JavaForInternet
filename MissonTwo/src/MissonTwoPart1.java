import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MissonTwoPart1 {

	private static LinkedList<String> entries = new LinkedList<>();
	private static Thread consumerThread;// �������߳�

	public static void main(String[] args) throws Exception {
		Map<String, MissonTwoPart3> flight = new ConcurrentHashMap<String, MissonTwoPart3>();// ����map

		PrintStream outfile = new PrintStream("./���۶�̬��ʾ(���̲߳��ṩ���¹���).txt");
		System.setOut(outfile);// �������Ŀ��
		System.out.println("| ����� \t\t| ������ \t\t| Ԥ�Ƶ��� \t| ʵ�ʵ��� \t| ��ע \t|");// \t��

		// �����������������߳�
		consumerThread = new Thread(() -> {
			try {
				processEntry(flight);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		consumerThread.start();

		// �����������������߳�
		new Thread(() -> {
			readDataFile();
		}).start();

	}

	private static void processEntry(Map<String, MissonTwoPart3> flight) throws FileNotFoundException, IOException {// �����ߵȴ��������߳�
		while (true) {
			synchronized (entries) {
				while (entries.isEmpty()) {
					try {
						entries.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
				// ��������ֻ��д������
				String entry = entries.remove(entries.size() - 1);
				if (entry.contains("ddtm")) {
					String str = entry;
					String[] info = new String[20];

					Pattern p_ffid = Pattern.compile("(?<=(ffid=)).*?-.*?(?=(-2018))");// ƥ�亽���,�� ffid= ��ͷ���� - 2018��β
					Matcher m_ffid = p_ffid.matcher(str);

					Pattern p_apcd1 = Pattern.compile("(?<=(arno=1, apcd=)).*?(?=(\\,))");// ƥ��ʼ��վ
					Matcher m_apcd1 = p_apcd1.matcher(str);

					Pattern p_felt = Pattern.compile("(?<=(felt=))\\d*?(?=(\\]|\\,))");// ƥ��Ԥ�Ƶ���ʱ��
					Matcher m_felt = p_felt.matcher(str);

					Pattern p_frlt = Pattern.compile("(?<=(frlt=))\\d*?(?=(\\]|\\,))");// ƥ��ʵ�ʵ���ʱ��
					Matcher m_frlt = p_frlt.matcher(str);

					Pattern p_ista = Pattern.compile("(?<=(ista=))\\d*?(?=(\\,))");// ƥ�亽��״̬
					Matcher m_ista = p_ista.matcher(str);

					Pattern p_flid = Pattern.compile("(?<=(flid=))\\d*?(?=(\\,))");// ƥ�亽���,�� flid= ��ͷ���� , ��β
					Matcher m_flid = p_flid.matcher(str);

					// ����properttiesƥ�����
					Properties airports = new Properties();
					airports.load(new FileReader("./airportnum.txt"));

					String[] planeinfo = new String[20];
					int planeno = 0;
					if (str != null) {
						// ���Һ����
						String line = new String();
						if (m_ffid.find()) {
							line = m_ffid.group();
							if (line.length() < 8)
								planeinfo[planeno++] = String.format("|%s\t\t", m_ffid.group());
							else
								planeinfo[planeno++] = String.format("|%s\t", m_ffid.group());

						}
						// ���ҳ�����
						if (m_apcd1.find()) {
							planeinfo[planeno++] = String.format("|" + airports.getProperty(m_apcd1.group()) + "\t");

						} else {
							planeinfo[planeno++] = String.format("|null\t\t");
						}
						// ����Ԥ�Ƶ���ʱ��
						if (m_felt.find()) {
							line = m_felt.group();
							planeinfo[planeno++] = String
									.format("|" + line.substring(8, 10) + ":" + line.substring(10, 12) + "\t");
						} else {
							planeinfo[planeno++] = String.format("|null\t");
						}
						// ����ʵ�ʵ���ʱ��
						if (m_frlt.find()) {
							line = m_frlt.group();
							planeinfo[planeno++] = String
									.format("|" + line.substring(8, 10) + ":" + line.substring(10, 12) + "\t");
						} else {
							planeinfo[planeno++] = String.format("|null\t");
						}
						// ����״̬
						if (m_ista.find()) {
							planeinfo[planeno++] = String.format("|%s\t", m_ista.group());

						} else {
							planeinfo[planeno++] = String.format("|null\t");
						}
						// ���Һ����ʶ
						if (m_flid.find()) {
							planeinfo[planeno++] = String.format("|%s\t", m_flid.group());
						} else {
							planeinfo[planeno++] = String.format("|null\t");
						}
					}
					info = planeinfo;
//					try {
//						info = MissonTwoPart2.Printplane(entry);
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}			
//					for(int i=0;i<info.length;i++)
//					{
//						if(info[i]!=null)
//						{
//							System.out.print(info[i]);
//						}
//					}

					MissonTwoPart3 flighter = new MissonTwoPart3();
					flighter.setFfid(info[0]);
					flighter.setFromwhere(info[1]);
					flighter.setFelt(info[2]);
					flighter.setFrlt(info[3]);
					flighter.setMark(info[4]);
					flighter.setFlid(info[5]);
					if (flight.containsKey(info[5])) {
						if(!flighter.getFromwhere().equals("|null\t\t"))//������ ��= ��������
							flight.get(info[5]).setFromwhere(info[1]);
						if(!flighter.getFelt().equals("|null\t"))
							flight.get(info[5]).setFelt(info[2]);
						if(!flighter.getFrlt().equals("|null\t"))
							flight.get(info[5]).setFrlt(info[3]);
						if(!flighter.getMark().equals("|null\t"))
							flight.get(info[5]).setMark(info[4]);

					} else {
						flight.put(info[5], flighter);// ����map
					}
					System.out.print(flight.get(info[5]).getFfid());
					System.out.print(flight.get(info[5]).getFromwhere());
					System.out.print(flight.get(info[5]).getFelt());
					System.out.print(flight.get(info[5]).getFrlt());
					System.out.print(flight.get(info[5]).getMark());
					System.out.println("|");
				}
//				System.out.println(entry);
			}
		}
	}

	private static void readDataFile() {// �����߶����ݲ��ύ��ENTRIES
		try (Scanner scanner = new Scanner(new File("fdsdata.txt"))) {
			while (true) {
				if (!scanner.hasNextLine()) {
					consumerThread.interrupt();
					break;
				}
				String entry = scanner.nextLine();
				synchronized (entries) {
					entries.add(0, entry);
					entries.notify();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
