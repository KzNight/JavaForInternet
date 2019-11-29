import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Demo02 extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel tm = new DefaultTableModel(new String[] {"航班号","form","time"},0);
	private JButton btnNewButton;
	private JScrollPane scrollPane;
//	DefaultTableCellRenderer表格渲染类
	/**
	 * 使用了SwingWorker避免EDT死掉
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Demo02 frame = new Demo02();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Demo02() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(91, 61, 258, 127);
		contentPane.add(scrollPane);
		
		table = new JTable(tm);
		scrollPane.setViewportView(table);
		
		final JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//创建并启动自己写的swing worker对象
				new ReceiveAndProcessData().execute();
				btnNewButton.setEnabled(false);
			}
		});
		btnNewButton.setBounds(166, 228, 93, 23);
		contentPane.add(btnNewButton);
	}
	//看这里！！！！！！！！！！！！！1
	class ReceiveAndProcessData extends SwingWorker<String, String[]>{
		//alt+/自动添加成员方法函数
		//建立新线程
		@Override
		protected String doInBackground() throws Exception {
			// TODO Auto-generated method stub
			try (Socket socket = new Socket("10.5.25.193", 9999); 
					Scanner scanner = new Scanner(socket.getInputStream())) {
				//开始从服务器读数据，注意服务器自己发数据有延迟
				String line = null;
				while(scanner.hasNextLine()) {
					line = scanner.nextLine();
					//分析数据在这里
					
					//假设分析数据结果：CA-1234 上海 2019.11.7
					final String flidstString = "CA-1234";//final是因为成员变量和局部变量的原因
					final String fromtString = "上海";
					final String timeString = "2019.11.7";
					//输出在这里
					publish(new String[] {flidstString, fromtString, timeString });//提交给process共EDT执行
					
//					此时就不要在后台耗时线程更新界面了，以下代码在DEMO2中抛弃
//					EventQueue.invokeLater(new Runnable() {
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							tm.addRow(new String[] { flidstString, fromtString, timeString });
//						}
//					});// 异步提交
//					tm.addRow(new String[] {flidstString,fromtString,timeString});//不应该在这个线程里改，应该提交给EDT
					System.out.println(line);//测试服务器是否正确连接
				}
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "飞机都飞走了";
		}
		@Override
		//在EDT上来实行
		protected void process(List<String[]> rows) {//改成了rows
			for(String[]row:rows) {
				tm.addRow(row);
			}
		}
		
	}
}
