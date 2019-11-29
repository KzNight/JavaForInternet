# KzNight
## 2019.11.7
* 以下所有内容仅针对[MissonThree](/MissonThree)项目文件
>Eclipse_Windows Builder_Jtable是一个重量级组件
使用了MVC(Model View Control) Model：数据 View：试图 Control：控制
本次实验采用default table model(设置默认表头)
任何组件都可以添加滚动条：右键 surround With，添
加jscrollpane，前提是改成absolute布局（ps:手写也行，jscrollpane，很难写）"title"

* 难点1：EDT：Event Dispatch Thread 事件分发线程

    控制界面的线程，按钮单击相应函数actionformed在EDT线程里。这个线程会贼忙，直到while循环结束，EDT才能响应其他按钮之类的界面
    后台耗时操作不能交给EDT线程。应该在函数里新开一个新的线程，参考demo1

* 难点2：如何在EDT之外的线程里，将更改界面的动作提交给EDT，避免界面更改“打架”

    EventQueue.invokeLater()异步事件提交，即实现后台耗时操作与前台界面更新之间的配合工作，参考demo1

* 难点3：实现后台耗时操作与前台界面更新之间的配合工作的类：SwingWorke

    在demo2中实现继承[swingworker](http://tool.oschina.net/docsearch?q=SwingWorker/)的类
    注意：实验三不使用hashmap，而是tablemodel，否则两者的一致性很难保证

* 难点4：如何使用默认布局（非absolute）添加滑动条，并加一个排序器

    用了老师的方法，具体看群里发的gif图片以及实验文档
* 难点5：使用DefaultTableCellRenderer表格渲染类

    表格渲染类中为了添加图标，在两个函数之间以List<Object>传输row
  class ReceiveAndProcessData extends SwingWorker<String, Object[]> { // 第一个没啥用，第二个是后台操作产生的用来更新其前台界面的中间数据（在本实验为了图标，使用Object）。
		// Alt+/ 会自动补全抽象类的方法
		// 自动开一个新线程完成后台耗时操作
		protected String doInBackground() throws Exception {
			try (Socket socket = new Socket("localhost", 1818);//从自己写的客户端
			//try (Socket socket = new Socket("10.5.25.193", 9999); // 从老师服务器
					Scanner scanner = new Scanner(socket.getInputStream())) {
				//	Scanner scanner = new Scanner(new FileReader("fdsdata1.txt"))) {
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
				//tm.addRow(row);
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
						//tm.removeRow(anchor);
					} // 删除
					if (!row[3].equals("null")) {
						tm.setValueAt(row[3], anchor, 3);
					}
					//if(!row[4].equals("null")){tm.setValueAt(row[4], anchor, 4);tm.setValueAt("已经抵达", anchor, 5);}//不删除而是改为已经抵达
				} else {// 如果tm中没有这个航班的信息，则新加入一行
					if (!row[3].equals("null"))
						row[5] = "即将抵达";
					if (!row[4].equals("null")) {
						row[5] = "已经抵达";
						//tm.removeRow(anchor);
					}
					if (row[3].equals("null") && row[4].equals("null"))
						break;
					tm.addRow(row);
				}
				progressBar.setValue(count);
			}
		}
*
完成了对航班状态的更新，可以关注到某些航班的数据从即将抵达便成为已经抵达
  没有写删除，觉得不应该立刻删除已经抵达的航班，而是显示一小会儿，涉及到ddtm，但是这个航班更新数据太快了，若是添加已抵达航班滞留时间，读起来太慢了，不知道有啥方法不可以将进度条持续放在最下面，即table只显示最新的这么多条数据，这样就只显示最近的已经到达的以及即将到达的航班。
