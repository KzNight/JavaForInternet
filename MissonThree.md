# KzNight
# 2019.11.7
Eclipse_Windows Builder_Jtable是一个重量级组件，使用了MVC(Model View Control)

Model：数据 View：试图 Control：控制

本次实验采用default table model(设置默认表头)

任何组件都可以添加滚动条：右键 surround With，添加jscrollpane，前提是改成absolute布局（ps:手写也行，jscrollpane，很难写）

# 难点1：EDT：Event Dispatch Thread 事件分发线程，控制界面的线程，按钮单击相应函数actionformed在EDT线程里。这个线程会贼忙，直到while循环结束，EDT才能响应其他按钮之类的界面
解决1：后台耗时操作不能交给EDT线程。应该在函数里新开一个新的线程，参考demo1

# 难点2：如何在EDT之外的线程里，将更改界面的动作提交给EDT，避免界面更改“打架”
解决2：EventQueue.invokeLater()异步事件提交，即实现后台耗时操作与前台界面更新之间的配合工作，参考demo1

# 难点3：实现后台耗时操作与前台界面更新之间的配合工作的类：SwingWorke
http://tool.oschina.net/docsearch?q=SwingWorker
在demo2中实现继承swingworker的类
注意：实验三不使用hashmap，而是tablemodel，否则两者的一致性很难保证

#难点4：如何使用默认布局（非absolute）添加滑动条

#难点5：使用DefaultTableCellRenderer表格渲染类
