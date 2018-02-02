package com.sun.mq.zeromq.proxy;

import java.util.concurrent.atomic.AtomicInteger;

import org.zeromq.ZMQ;

/**
 *
 */
public class RequestClient {
	public static void main(String args[]) {
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socket = context.socket(ZMQ.REQ);

		socket.connect("tcp://*:12345");
		long begin = System.currentTimeMillis();
		byte[] response;
		while (!Thread.currentThread().isInterrupted()) {
			socket.send("from client");
			response = socket.recv();
			System.out.println(new String(response));
		}
		socket.close();
		context.term();
	}
}
