import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class Demo01 extends JFrame {

	private JPanel contentPane;
	private JTable table;
	
	/**
	 * MVC采用defaulttablemodel
	 */
	private DefaultTableModel tm = new DefaultTableModel(new String[] {"航班号","form","time"},0);
	private JButton btnStart;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Demo01 frame = new Demo01();
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
	public Demo01() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 610, 415);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 5, 584, 343);
		contentPane.add(scrollPane);
		
		table = new JTable(tm);
		scrollPane.setViewportView(table);
		
		btnStart = new JButton("start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//因为EDT的原因，新建线程处理
				new Thread(new Runnable(){		
					@Override
					public void run() {
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
								EventQueue.invokeLater(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										tm.addRow(new String[] {flidstString,fromtString,timeString});
									}
								});//异步提交
//								tm.addRow(new String[] {flidstString,fromtString,timeString});//不应该在这个线程里改，应该提交给EDT
								System.out.println(line);//测试服务器是否正确连接
							}
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// TODO Auto-generated method stub
						
					}
				}).start();
			}
		});
		btnStart.setBounds(5, 348, 584, 23);
		contentPane.add(btnStart);
	}
}
