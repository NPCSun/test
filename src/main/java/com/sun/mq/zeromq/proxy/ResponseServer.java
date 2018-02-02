package com.sun.mq.zeromq.proxy;

import org.zeromq.ZMQ;

/**
 *
 */
public class ResponseServer {
	public static void main (String[] args) {
		ZMQ.Context context = ZMQ.context(1);

		ZMQ.Socket socket = context.socket(ZMQ.REP);
		socket.connect ("tcp://*:54321");
		byte[] request;
		String response = "from server";
		while (!Thread.currentThread().isInterrupted()) {
			request = socket.recv();
			System.out.println(new String(request));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			socket.send(response.getBytes());
		}
		socket.close();  //先关闭socket
		context.term();  //关闭当前的上下文
	}
}