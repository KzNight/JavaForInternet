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
			PrintStream outfile = new PrintStream("./���۶�̬��ʾ.txt");
			System.setOut(outfile);//�ı��ļ������Ϊ�ļ���ע�͵�������ʾ��console��
			System.out.println("| ����� \t\t| ������\t\t| Ԥ�Ƶ���\t| ʵ�ʵ���\t| ��ע\t|");// \t���Ʊ��
			while((line = br.readLine()) != null ) {
				if(line.contains("ddtm")) {
					String []info = new String[20];
					info = MissonOnePart2.Printplane(line);//����ÿһ�е���Ϣ�����ص�����info��
					
					for(int i=0;i<info.length;i++)//ѭ�����info��ÿһ�����
					{
						if(info[i]!=null)//���info���±�����ݲ�Ϊ����д�뵽�ı�
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
