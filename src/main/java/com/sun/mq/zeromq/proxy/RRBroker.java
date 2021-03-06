package com.sun.mq.zeromq.proxy;

import org.zeromq.ZMQ;

/**
 * Created by sun on 2018/1/19 下午2:48.
 */
public class RRBroker {
	public static void main(String[] args) {
		//  Prepare our context and sockets
		ZMQ.Context context = ZMQ.context(1);

		ZMQ.Socket frontend = context.socket(ZMQ.ROUTER);
		ZMQ.Socket backend = context.socket(ZMQ.DEALER);
		frontend.bind("tcp://*:5559");
		backend.bind("tcp://*:5560");

		System.out.println("launch and connect broker.");

		//  Initialize poll set
		ZMQ.Poller items = new ZMQ.Poller(2);
		items.register(frontend, ZMQ.Poller.POLLIN);
		items.register(backend, ZMQ.Poller.POLLIN);

		boolean more = false;
		byte[] message;

		//  Switch messages between sockets
		while (!Thread.currentThread().isInterrupted()) {
			//  poll and memorize multipart detection
			items.poll();

			if (items.pollin(0)) {
				while (true) {
					// receive message
					message = frontend.recv(0);
					more = frontend.hasReceiveMore();

					// Broker it
					backend.send(message, more ? ZMQ.SNDMORE : 0);
					if (!more) {
						break;
					}
				}
			}
			if (items.pollin(1)) {
				while (true) {
					// receive message
					message = backend.recv(0);
					more = backend.hasReceiveMore();
					// Broker it
					frontend.send(message, more ? ZMQ.SNDMORE : 0);
					if (!more) {
						break;
					}
				}
			}
		}
		//  We never get here but clean up anyhow
		frontend.close();
		backend.close();
		context.term();
	}
}
