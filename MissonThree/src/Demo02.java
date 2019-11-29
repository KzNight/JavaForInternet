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
	private DefaultTableModel tm = new DefaultTableModel(new String[] {"�����","form","time"},0);
	private JButton btnNewButton;
	private JScrollPane scrollPane;
//	DefaultTableCellRenderer�����Ⱦ��
	/**
	 * ʹ����SwingWorker����EDT����
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
				//�����������Լ�д��swing worker����
				new ReceiveAndProcessData().execute();
				btnNewButton.setEnabled(false);
			}
		});
		btnNewButton.setBounds(166, 228, 93, 23);
		contentPane.add(btnNewButton);
	}
	//�����������������������������1
	class ReceiveAndProcessData extends SwingWorker<String, String[]>{
		//alt+/�Զ���ӳ�Ա��������
		//�������߳�
		@Override
		protected String doInBackground() throws Exception {
			// TODO Auto-generated method stub
			try (Socket socket = new Socket("10.5.25.193", 9999); 
					Scanner scanner = new Scanner(socket.getInputStream())) {
				//��ʼ�ӷ����������ݣ�ע��������Լ����������ӳ�
				String line = null;
				while(scanner.hasNextLine()) {
					line = scanner.nextLine();
					//��������������
					
					//����������ݽ����CA-1234 �Ϻ� 2019.11.7
					final String flidstString = "CA-1234";//final����Ϊ��Ա�����;ֲ�������ԭ��
					final String fromtString = "�Ϻ�";
					final String timeString = "2019.11.7";
					//���������
					publish(new String[] {flidstString, fromtString, timeString });//�ύ��process��EDTִ��
					
//					��ʱ�Ͳ�Ҫ�ں�̨��ʱ�̸߳��½����ˣ����´�����DEMO2������
//					EventQueue.invokeLater(new Runnable() {
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							tm.addRow(new String[] { flidstString, fromtString, timeString });
//						}
//					});// �첽�ύ
//					tm.addRow(new String[] {flidstString,fromtString,timeString});//��Ӧ��������߳���ģ�Ӧ���ύ��EDT
					System.out.println(line);//���Է������Ƿ���ȷ����
				}
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "�ɻ���������";
		}
		@Override
		//��EDT����ʵ��
		protected void process(List<String[]> rows) {//�ĳ���rows
			for(String[]row:rows) {
				tm.addRow(row);
			}
		}
		
	}
}
