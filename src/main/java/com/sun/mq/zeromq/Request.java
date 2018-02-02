package com.sun.mq.zeromq;

import java.util.concurrent.atomic.AtomicInteger;

import org.zeromq.ZMQ;

/**
 * RequestClient-对应请求回应模型
 * Created by sun on 2018/1/12 上午11:06.
 */
public class Request {
	public static void main(String args[]) {
		final AtomicInteger number = new AtomicInteger(0);
		for (int j = 0;  j < 1; j++) {
			new Thread(new Runnable(){
				private int count = 0;
				public void run() {
					// TODO Auto-generated method stub
					ZMQ.Context context = ZMQ.context(1);  //创建一个I/O线程的上下文
					ZMQ.Socket socket = context.socket(ZMQ.REQ);   //创建一个request类型的socket，这里可以将其简单的理解为客户端，用于向response端发送数据

					socket.connect("tcp://127.0.0.1:5555");   //与response端建立连接
					long begin = System.currentTimeMillis();
					byte[] response;
					while(true) {
						String request = "你好";
						socket.send(request.getBytes());   //向reponse端发送数据
						response = socket.recv();   //接收response发送回来的数据  正在request/response模型中，send之后必须要recv之后才能继续send，这可能是为了保证整个request/response的流程走完
						count++;
						int now = number.incrementAndGet();
						if (now % 10000 == 0) {
							System.out.println(now + "  count is : " + count);
							long end = System.currentTimeMillis();
							System.out.println("耗时:\t" + ((end-begin)));
							begin = end;
						}
						if(now == 100000){
							socket.close();
							context.term();
							break;
						}
					}
				}
			}).start();;
		}
	}
}
