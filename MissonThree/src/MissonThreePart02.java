import java.awt.Container;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class MissonThreePart02 {
	public MissonThreePart02() throws Exception{
		// TODO Auto-generated constructor stub
	}
	 public static String[] Printplane(String str) throws FileNotFoundException, IOException {
		 Pattern p_ffid=Pattern.compile("(?<=(ffid=)).*?-.*?(?=(-2018))");//匹配航班号,以 ffid= 开头，以 - 结尾
		 Matcher m_ffid=p_ffid.matcher(str);
		 
		 Pattern p_apcd1=Pattern.compile("(?<=(arno=1, apcd=)).*?(?=(\\,))");//匹配始发站
		 Matcher m_apcd1=p_apcd1.matcher(str);
		 
		 Pattern p_felt=Pattern.compile("(?<=(felt=))\\d*?(?=(\\]|\\,))");//匹配预计到达时间
		 Matcher m_felt=p_felt.matcher(str);
		 
		 Pattern p_frlt=Pattern.compile("(?<=(frlt=))\\d*?(?=(\\]|\\,))");//匹配实际到达时间
		 Matcher m_frlt=p_frlt.matcher(str);
		 
		 Pattern p_ista=Pattern.compile("(?<=(ista=))\\d*?(?=(\\,))");//匹配航班状态
		 Matcher m_ista=p_ista.matcher(str);
		 
		 //利用propertties匹配机场
		 Properties airports = new Properties();
		 airports.load(new FileReader("airportnum.txt"));
		 
		 String [] planeinfo = new String[20];
		 int planeno = 1;
		 if(str != null) {
			    //查找航班号
			    String line = new String();
				if(m_ffid.find())			
				{
					line = m_ffid.group();
					if(line.length() < 8)
					planeinfo[planeno++] = String.format("%s", m_ffid.group());
					else planeinfo[planeno++] = String.format("%s", m_ffid.group());
					
				}
				//查找出发地
				if(m_apcd1.find())
				{
					planeinfo[planeno++] = String.format(airports.getProperty(m_apcd1.group()));	
		
				}else {planeinfo[planeno++] = String.format("null");}
				//查找预计到达时间
				if(m_felt.find())
				{
					line = m_felt.group();
					planeinfo[planeno++] = String.format(line.substring(8,10)+":"+line.substring(10,12));
				}else {planeinfo[planeno++] = String.format("null");}
				//查找实际到达时间
				if(m_frlt.find())
				{
					line = m_frlt.group();
					planeinfo[planeno++] = String.format(line.substring(8,10)+":"+line.substring(10,12));
				}else {planeinfo[planeno++] = String.format("null");}
				//查找状态
				if(m_ista.find())			
				{
					planeinfo[planeno++] = String.format("%s", m_ista.group());
					
				}else {planeinfo[planeno++] = String.format("null");}
		 }
		 return planeinfo;
			
	 }
}
