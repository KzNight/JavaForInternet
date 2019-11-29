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
 * @date 2019年11月25日
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
	 * 以下是一个小的公用方法
	 */
	// 缩放图片函数，用以控制图片的显示大小
	public ImageIcon change(ImageIcon image, double i) {// i 为放缩的倍数
		int width = (int) (image.getIconWidth() * i);
		int height = (int) (image.getIconHeight() * i);
		Image img;
		if (width != 0 && height != 0) {
			img = image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);// 第三个值可以去查api是图片转化的方式
			ImageIcon image2 = new ImageIcon(img);
			return image2;
		}
		return image;
	}

	/**
	 * 以下是构造函数
	 */
	public MissonThreePart01() {
		String[] columnNames = new String[] { "航空公司图标", "航班号 ", "来自于 ", "预计到达", "实际到达", "备注 " };
		tm = new DefaultTableModel(columnNames, 0) {
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) { // 将是图片的那一列的返回值为图片,其他为字符串
					return ImageIcon.class;
				} else {
					return String.class;
				}
			}
		}; // 第一个参数是表头

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
		table.setFont(new Font("宋体", Font.BOLD, 17));
		table.getTableHeader().setFont(new Font("宋体", Font.PLAIN, 19));
		scrollPane.setViewportView(table);

		Dimension size = table.getTableHeader().getPreferredSize();
		size.height = 33;// 设置新的表头高度32
		table.getTableHeader().setPreferredSize(size);
		table.setBackground(new Color(0, 102, 153));// 设置表格背景颜色

		table.getTableHeader().setBackground(new Color(0, 51, 102));
		table.getTableHeader().setForeground(new Color(255, 255, 255));
		table.setRowHeight(55);// 设置行高

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(tm);
		table.setRowSorter(sorter);// 完成表格排序功能
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				setBackground(new Color(0, 51, 255)); // 设置背景
				if ("已经抵达".equals(value) && column == 5) {// 通过备注的字符串值来设置备注的字体颜色
					setForeground(new Color(255, 51, 0));
				} else {
					if ("即将抵达".equals(value) && column == 5) {
						setForeground(new Color(0, 255, 0));
					} else
						setForeground(new Color(255, 255, 255));
				}
				setHorizontalAlignment(SwingConstants.CENTER); // 居中显示
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};
		for (int i = 1; i < columnNames.length; i++) { // 将每一列都加上CellRenderer
			table.getColumn(columnNames[i]).setCellRenderer(cellRenderer);
		}
		// 进行字体,行高等设置
		table.setRowHeight(55);

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		table.setFont(new Font("黑体", Font.BOLD, 20));
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

	// 写一个Swing内部类，用来接受并分析数据
	class ReceiveAndProcessData extends SwingWorker<String, Object[]> { // 第一个没啥用，第二个是后台操作产生的用来更新其前台界面的中间数据（在本实验为了图标，使用Object）。
		// Alt+/ 会自动补全抽象类的方法
		// 自动开一个新线程完成后台耗时操作
		protected String doInBackground() throws Exception {
			try (Socket socket = new Socket("localhost", 1818);//从自己写的客户端
//			try (Socket socket = new Socket("10.5.25.193", 9999); // 从老师服务器
					Scanner scanner = new Scanner(socket.getInputStream())) {
//					Scanner scanner = new Scanner(new FileReader("fdsdata1.txt"))) {
				// 以下分析数据
				count = 0;
				String line;
				while (scanner.hasNextLine()) {
					count += 1;// 进度条加一
					line = scanner.nextLine();
					if (line.contains("ddtm")) {
						String[] info = new String[20];
						info = MissonThreePart02.Printplane(line);// 分析每一行的信息并返回到数组info里
						Object[] infoObjects = new Object[6];// 定义数组来放置中间值
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

		// process是在EDT中执行的，用来更新界面

		protected void process(List<Object[]> rows) {// 注意！仅在该线程中完成对表格数据的更改，避免多线程造成的混乱
			String imageString = "";
			boolean isHad = false;
			int anchor = 0;
			for (Object[] row : rows) {// 注意这里是Object方便接受imageicon
//				tm.addRow(row);
				String airlines = ((String) row[1]).substring(0, 2);
				imageString = "D:\\Java\\MissonThree\\image\\" + airlines + ".jpg";
				ImageIcon icon = new ImageIcon(imageString);
				if (icon.getImage() != null) {
					ImageIcon image = change(icon, 0.2);
					row[0] = image;
				}
				for (anchor = 0; anchor < tm.getRowCount(); anchor++) { // 遍历tm，判断tm中是否已经有传过来的航班 anchor 行号
					if (row[1].equals(tm.getValueAt(anchor, 1))) {
						isHad = true;
						break;
					}
				}
				if (isHad) { // 如果tm中已有这个航班的信息
					if (!row[2].equals("null")) {
						tm.setValueAt(row[2], anchor, 2);
					}
					if (!row[4].equals("null")) {
						tm.setValueAt(row[4], anchor, 4);
						tm.setValueAt("已经抵达", anchor, 5);
//						tm.removeRow(anchor);
					} // 删除
					if (!row[3].equals("null")) {
						tm.setValueAt(row[3], anchor, 3);
					}
//					if(!row[4].equals("null")){tm.setValueAt(row[4], anchor, 4);tm.setValueAt("已经抵达", anchor, 5);}//不删除而是改为已经抵达
				} else {// 如果tm中没有这个航班的信息，则新加入一行
					if (!row[3].equals("null"))
						row[5] = "即将抵达";
					if (!row[4].equals("null")) {
						row[5] = "已经抵达";
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
