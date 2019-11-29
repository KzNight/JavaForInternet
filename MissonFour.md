# KzNight
## 2019.11.7
* 以下所有内容仅针对[MissonThree](/MissonFour)项目文件

      本次实验完成NIO的服务器程序，并配合第三次实验的客户端程序使用

* 1）用非阻塞（而不是多线程或线程池）来实现并发服务器。

    这个比较好实现，hi用教材364页以及老师提供的服务器写法即可完成
    具体去看自己的笔记

		 非阻塞服务器编程通用框架，reactor，multi（多路复用器）
      while(true){
		     if(selector.select()>0){//等待直到有就绪事件 //或许相关事件已发生的selection集合 Set<SelectionKey>
		        readkeys = selector.selectedKeys();
             Iterator<SelectionKey> iterator =readkeys,iterator();//迭代器迭代 while(iterator.hasNext()){
                SelectionKey key =iterator.next();
                iterator.remove(); //以下看究竟哪种准备好了：接受链接就绪事件、读就绪事件、写就绪事件
		        if(key.isAcceptable()){ //接受客户端连接请求 }
            if(key.isReadable()){ //从客户端读 }
		        if(key.isWritable()){ //向客户端写 } } } }

		 //实现接受连接、读、写的函数

* 2）四个以上的客户端可以同时从服务器接收数据，注意是同时接收数据，每个客户端接收数据的进度不同，但绝不是后来的客户端要等前面的客户端接收完全部数据后才能接收到数据。3）内存占用越少越好，所以尽量不要为每个客户端创建一个航显数据文件的文件输入流，也不要为每个客户端创建一个字节数组来装航显数据文件内容。

  使用一个bigbuffer读完全部文件，在接受事件中复制一次，在写事件中将上个复制的再复制一个临时的buffer用于写一句。注意去看自己的注释，写的很明白
* 4）用实验3写的客户端来测试。

* 5）支持加时间延迟，比如每发送一行（或每发送64个字节），延时10ms。
