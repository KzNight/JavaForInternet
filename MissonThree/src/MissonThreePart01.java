import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Kz_Night
 * @date 2019��11��25��
 * 
 */
public class MissonThreePart01 extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private int count;
	private ReceiveAndProcessData receiveAndProcessData;
	private DefaultTableModel tm;
	private JProgressBar progressBar;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MissonThreePart01 frame = new MissonThreePart01();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * ������һ��С�Ĺ��÷���
	 */
	// ����ͼƬ���������Կ���ͼƬ����ʾ��С
	public ImageIcon change(ImageIcon image, double i) {// i Ϊ�����ı���
		int width = (int) (image.getIconWidth() * i);
		int height = (int) (image.getIconHeight() * i);
		Image img;
		if (width != 0 && height != 0) {
			img = image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);// ������ֵ����ȥ��api��ͼƬת���ķ�ʽ
			ImageIcon image2 = new ImageIcon(img);
			return image2;
		}
		return image;
	}

	/**
	 * �����ǹ��캯��
	 */
	public MissonThreePart01() {
		String[] columnNames = new String[] { "���չ�˾ͼ��", "����� ", "������ ", "Ԥ�Ƶ���", "ʵ�ʵ���", "��ע " };
		tm = new DefaultTableModel(columnNames, 0) {
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) { // ����ͼƬ����һ�еķ���ֵΪͼƬ,����Ϊ�ַ���
					return ImageIcon.class;
				} else {
					return String.class;
				}
			}
		}; // ��һ�������Ǳ�ͷ

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 629);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		table = new JTable(tm);
//		scrollPane.setViewportView(table);
		table.setForeground(new Color(255, 255, 255));
		table.setFont(new Font("����", Font.BOLD, 17));
		table.getTableHeader().setFont(new Font("����", Font.PLAIN, 19));
		scrollPane.setViewportView(table);

		Dimension size = table.getTableHeader().getPreferredSize();
		size.height = 33;// �����µı�ͷ�߶�32
		table.getTableHeader().setPreferredSize(size);
		table.setBackground(new Color(0, 102, 153));// ���ñ�񱳾���ɫ

		table.getTableHeader().setBackground(new Color(0, 51, 102));
		table.getTableHeader().setForeground(new Color(255, 255, 255));
		table.setRowHeight(55);// �����и�

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(tm);
		table.setRowSorter(sorter);// ��ɱ��������
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				setBackground(new Color(0, 51, 255)); // ���ñ���
				if ("�Ѿ��ִ�".equals(value) && column == 5) {// ͨ����ע���ַ���ֵ�����ñ�ע��������ɫ
					setForeground(new Color(255, 51, 0));
				} else {
					if ("�����ִ�".equals(value) && column == 5) {
						setForeground(new Color(0, 255, 0));
					} else
						setForeground(new Color(255, 255, 255));
				}
				setHorizontalAlignment(SwingConstants.CENTER); // ������ʾ
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};
		for (int i = 1; i < columnNames.length; i++) { // ��ÿһ�ж�����CellRenderer
			table.getColumn(columnNames[i]).setCellRenderer(cellRenderer);
		}
		// ��������,�иߵ�����
		table.setRowHeight(55);

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		table.setFont(new Font("����", Font.BOLD, 20));
		table.getAutoCreateRowSorter();
		scrollPane.setViewportView(table);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);

		JButton btnStart = new JButton("\u5F00\u59CB");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tm.setRowCount(0);
				receiveAndProcessData = new ReceiveAndProcessData();
				receiveAndProcessData.execute();
				btnStart.setEnabled(false);
			}
		});
		panel.add(btnStart);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut);

		JButton btnStop = new JButton("\u505C\u6B62");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				receiveAndProcessData.cancel(false);
				btnStop.setEnabled(true);
			}
		});
		panel.add(btnStop);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut_1);

		JButton btnEnd = new JButton("\u5173\u95ED");
		btnEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(getDefaultCloseOperation());
			}
		});
		panel.add(btnEnd);

		progressBar = new JProgressBar();
		progressBar.setForeground(Color.GREEN);
		progressBar.setStringPainted(true);
		progressBar.setMaximum(18018);
		progressBar.setBackground(Color.WHITE);
		panel.add(progressBar);
	}

	// дһ��Swing�ڲ��࣬�������ܲ���������
	class ReceiveAndProcessData extends SwingWorker<String, Object[]> { // ��һ��ûɶ�ã��ڶ����Ǻ�̨��������������������ǰ̨������м����ݣ��ڱ�ʵ��Ϊ��ͼ�꣬ʹ��Object����
		// Alt+/ ���Զ���ȫ������ķ���
		// �Զ���һ�����߳���ɺ�̨��ʱ����
		protected String doInBackground() throws Exception {
			try (Socket socket = new Socket("localhost", 1818);//���Լ�д�Ŀͻ���
//			try (Socket socket = new Socket("10.5.25.193", 9999); // ����ʦ������
					Scanner scanner = new Scanner(socket.getInputStream())) {
//					Scanner scanner = new Scanner(new FileReader("fdsdata1.txt"))) {
				// ���·�������
				count = 0;
				String line;
				while (scanner.hasNextLine()) {
					count += 1;// ��������һ
					line = scanner.nextLine();
					if (line.contains("ddtm")) {
						String[] info = new String[20];
						info = MissonThreePart02.Printplane(line);// ����ÿһ�е���Ϣ�����ص�����info��
						Object[] infoObjects = new Object[6];// ���������������м�ֵ
						infoObjects[0] = null;
						infoObjects[1] = info[1];
						infoObjects[2] = info[2];
						infoObjects[3] = info[3];
						infoObjects[4] = info[4];
						infoObjects[5] = info[5];
						publish(infoObjects);
					}
				}
			}
			return "flight end";
		}

		// process����EDT��ִ�еģ��������½���

		protected void process(List<Object[]> rows) {// ע�⣡���ڸ��߳�����ɶԱ�����ݵĸ��ģ�������߳���ɵĻ���
			String imageString = "";
			boolean isHad = false;
			int anchor = 0;
			for (Object[] row : rows) {// ע��������Object�������imageicon
//				tm.addRow(row);
				String airlines = ((String) row[1]).substring(0, 2);
				imageString = "D:\\Java\\MissonThree\\image\\" + airlines + ".jpg";
				ImageIcon icon = new ImageIcon(imageString);
				if (icon.getImage() != null) {
					ImageIcon image = change(icon, 0.2);
					row[0] = image;
				}
				for (anchor = 0; anchor < tm.getRowCount(); anchor++) { // ����tm���ж�tm���Ƿ��Ѿ��д������ĺ��� anchor �к�
					if (row[1].equals(tm.getValueAt(anchor, 1))) {
						isHad = true;
						break;
					}
				}
				if (isHad) { // ���tm����������������Ϣ
					if (!row[2].equals("null")) {
						tm.setValueAt(row[2], anchor, 2);
					}
					if (!row[4].equals("null")) {
						tm.setValueAt(row[4], anchor, 4);
						tm.setValueAt("�Ѿ��ִ�", anchor, 5);
//						tm.removeRow(anchor);
					} // ɾ��
					if (!row[3].equals("null")) {
						tm.setValueAt(row[3], anchor, 3);
					}
//					if(!row[4].equals("null")){tm.setValueAt(row[4], anchor, 4);tm.setValueAt("�Ѿ��ִ�", anchor, 5);}//��ɾ�����Ǹ�Ϊ�Ѿ��ִ�
				} else {// ���tm��û������������Ϣ�����¼���һ��
					if (!row[3].equals("null"))
						row[5] = "�����ִ�";
					if (!row[4].equals("null")) {
						row[5] = "�Ѿ��ִ�";
//						tm.removeRow(anchor);
					}
					if (row[3].equals("null") && row[4].equals("null"))
						break;
					tm.addRow(row);
				}
				progressBar.setValue(count);
			}
		}
	}
}
