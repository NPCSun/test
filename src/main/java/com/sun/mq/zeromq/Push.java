package com.sun.mq.zeromq;

import org.zeromq.ZMQ;

/**
 * Created by sun on 2018/1/12 下午2:27.
 */
public class Push {
	public static void main(String args[]) {

		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket push  = context.socket(ZMQ.PUSH);
		push.bind("ipc://message-server");

		//百万消息推送
		for (int i = 0; i < 1000001; i++) {
			push.send("hello".getBytes());
		}
		//stop
		push.close();
		context.term();

	}
}
