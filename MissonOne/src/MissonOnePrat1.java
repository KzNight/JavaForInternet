import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MissonOnePrat1 {
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new FileReader("FDSdata1.txt"));
			String line = null;
			PrintStream outfile = new PrintStream("./进港动态显示.txt");
			System.setOut(outfile);//改变文件输出流为文件，注释掉就是显示在console中
			System.out.println("| 航班号 \t\t| 来自于\t\t| 预计到达\t| 实际到达\t| 备注\t|");// \t是制表符
			while((line = br.readLine()) != null ) {
				if(line.contains("ddtm")) {
					String []info = new String[20];
					info = MissonOnePart2.Printplane(line);//分析每一行的信息并返回到数组info里
					
					for(int i=0;i<info.length;i++)//循环输出info的每一个结果
					{
						if(info[i]!=null)//如果info该下标的内容不为空则写入到文本
						{
							System.out.print(info[i]);
						}
					}
					System.out.println("|");
				}
//				if(line.contains("DFME_ARRE")) {
//					if(line.contains("frlt=")) {
//						System.out.println("\t|\t|null\t|"+line.substring(13,15)+":"+line.substring(15,17)+"\t|");
//					}
//				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
