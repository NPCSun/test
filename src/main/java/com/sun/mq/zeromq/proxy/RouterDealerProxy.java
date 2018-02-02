package com.sun.mq.zeromq.proxy;

import org.zeromq.ZMQ;

/**
 * Created by sun on 2018/1/13 下午4:51.
 */
public class RouterDealerProxy {

	public static void main(String[] args) {
		ZMQ.Context context = ZMQ.context(1);

		ZMQ.Socket frontend  = context.socket(ZMQ.ROUTER);
		ZMQ.Socket backend  = context.socket(ZMQ.DEALER);
		frontend.bind("tcp://*:12345");
		backend.bind("tcp://*:54321");
		boolean flag = ZMQ.proxy(frontend, backend, null);
		System.out.println("start:\t" + flag);

		frontend.close();
		backend.close();
		context.term();
	}
}
