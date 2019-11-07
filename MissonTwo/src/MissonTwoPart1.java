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
	private static Thread consumerThread;// 消费者线程

	public static void main(String[] args) throws Exception {
		Map<String, MissonTwoPart3> flight = new ConcurrentHashMap<String, MissonTwoPart3>();// 建立map

		PrintStream outfile = new PrintStream("./进港动态显示(多线程并提供更新功能).txt");
		System.setOut(outfile);// 设置输出目标
		System.out.println("| 航班号 \t\t| 来自于 \t\t| 预计到达 \t| 实际到达 \t| 备注 \t|");// \t

		// 创建并启动消费者线程
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

		// 创建并启动生产者线程
		new Thread(() -> {
			readDataFile();
		}).start();

	}

	private static void processEntry(Map<String, MissonTwoPart3> flight) throws FileNotFoundException, IOException {// 消费者等待生产者线程
		while (true) {
			synchronized (entries) {
				while (entries.isEmpty()) {
					try {
						entries.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
				// 处理数据只能写在这里
				String entry = entries.remove(entries.size() - 1);
				if (entry.contains("ddtm")) {
					String str = entry;
					String[] info = new String[20];

					Pattern p_ffid = Pattern.compile("(?<=(ffid=)).*?-.*?(?=(-2018))");// 匹配航班号,以 ffid= 开头，以 - 2018结尾
					Matcher m_ffid = p_ffid.matcher(str);

					Pattern p_apcd1 = Pattern.compile("(?<=(arno=1, apcd=)).*?(?=(\\,))");// 匹配始发站
					Matcher m_apcd1 = p_apcd1.matcher(str);

					Pattern p_felt = Pattern.compile("(?<=(felt=))\\d*?(?=(\\]|\\,))");// 匹配预计到达时间
					Matcher m_felt = p_felt.matcher(str);

					Pattern p_frlt = Pattern.compile("(?<=(frlt=))\\d*?(?=(\\]|\\,))");// 匹配实际到达时间
					Matcher m_frlt = p_frlt.matcher(str);

					Pattern p_ista = Pattern.compile("(?<=(ista=))\\d*?(?=(\\,))");// 匹配航班状态
					Matcher m_ista = p_ista.matcher(str);

					Pattern p_flid = Pattern.compile("(?<=(flid=))\\d*?(?=(\\,))");// 匹配航班号,以 flid= 开头，以 , 结尾
					Matcher m_flid = p_flid.matcher(str);

					// 利用propertties匹配机场
					Properties airports = new Properties();
					airports.load(new FileReader("./airportnum.txt"));

					String[] planeinfo = new String[20];
					int planeno = 0;
					if (str != null) {
						// 查找航班号
						String line = new String();
						if (m_ffid.find()) {
							line = m_ffid.group();
							if (line.length() < 8)
								planeinfo[planeno++] = String.format("|%s\t\t", m_ffid.group());
							else
								planeinfo[planeno++] = String.format("|%s\t", m_ffid.group());

						}
						// 查找出发地
						if (m_apcd1.find()) {
							planeinfo[planeno++] = String.format("|" + airports.getProperty(m_apcd1.group()) + "\t");

						} else {
							planeinfo[planeno++] = String.format("|null\t\t");
						}
						// 查找预计到达时间
						if (m_felt.find()) {
							line = m_felt.group();
							planeinfo[planeno++] = String
									.format("|" + line.substring(8, 10) + ":" + line.substring(10, 12) + "\t");
						} else {
							planeinfo[planeno++] = String.format("|null\t");
						}
						// 查找实际到达时间
						if (m_frlt.find()) {
							line = m_frlt.group();
							planeinfo[planeno++] = String
									.format("|" + line.substring(8, 10) + ":" + line.substring(10, 12) + "\t");
						} else {
							planeinfo[planeno++] = String.format("|null\t");
						}
						// 查找状态
						if (m_ista.find()) {
							planeinfo[planeno++] = String.format("|%s\t", m_ista.group());

						} else {
							planeinfo[planeno++] = String.format("|null\t");
						}
						// 查找航班标识
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
						if(!flighter.getFromwhere().equals("|null\t\t"))//不能用 ！= 差点坑死我
							flight.get(info[5]).setFromwhere(info[1]);
						if(!flighter.getFelt().equals("|null\t"))
							flight.get(info[5]).setFelt(info[2]);
						if(!flighter.getFrlt().equals("|null\t"))
							flight.get(info[5]).setFrlt(info[3]);
						if(!flighter.getMark().equals("|null\t"))
							flight.get(info[5]).setMark(info[4]);

					} else {
						flight.put(info[5], flighter);// 存入map
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

	private static void readDataFile() {// 生产者读数据并提交在ENTRIES
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
