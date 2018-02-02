package com.sun.mq.zeromq;

import org.zeromq.ZMQ;

/**
 * ResponseServer-对应请求回应模型
 * Created by sun on 2018/1/12 上午11:06.
 */
public class Response {
	public static void main (String[] args) {
		ZMQ.Context context = ZMQ.context(1);  //这个表示创建用于一个I/O线程的context

		ZMQ.Socket socket = context.socket(ZMQ.REP);  //创建一个response类型的socket，他可以接收request发送过来的请求，其实可以将其简单的理解为服务端
		socket.bind ("tcp://*:5555");    //绑定端口
		int number = 0;
		byte[] request;
		String response = "world";
		while (!Thread.currentThread().isInterrupted()) {
			request = socket.recv();  //获取request发送过来的数据
			socket.send(response.getBytes());  //向request端发送数据  ，必须要要request端返回数据，没有返回就又recv，将会出错，这里可以理解为强制要求走完整个request/response流程
		}
		socket.close();  //先关闭socket
		context.term();  //关闭当前的上下文
	}
}