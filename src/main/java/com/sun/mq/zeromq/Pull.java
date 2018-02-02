package com.sun.mq.zeromq;

import java.util.concurrent.atomic.AtomicInteger;

import org.zeromq.ZMQ;

/**
 * Created by sun on 2018/1/12 下午2:28.
 */
public class Pull {
	public static void main(String args[]) {
		final AtomicInteger number = new AtomicInteger(0);
		for (int i = 0; i < 1; i++) {
			new Thread(new Runnable(){
				private int count = 0;
				public void run() {
					// TODO Auto-generated method stub
					ZMQ.Context context = ZMQ.context(1);
					ZMQ.Socket pull = context.socket(ZMQ.PULL);
					pull.connect("ipc://message-server");
					//
					long begin = System.currentTimeMillis();
					while (true) {
						String message = new String(pull.recv());
						int now = number.incrementAndGet();
						count++;
						if (now % 1000000 == 0) {
							System.out.println(now + "  count is : " + count);
							long end = System.currentTimeMillis();
							System.out.println("耗时:\t" + ((end-begin)));
							begin = end;
						}
						if(now == 1000000){
							break;
						}
					}
				}

			}).start();
		}
	}
}
