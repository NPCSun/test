/*package com.sun.mq;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.MetaClientConfig;
import com.taobao.metamorphosis.client.MetaMessageSessionFactory;
import com.taobao.metamorphosis.client.producer.MessageProducer;
import com.taobao.metamorphosis.client.producer.SendResult;
import com.taobao.metamorphosis.exception.MetaClientException;
import com.taobao.metamorphosis.utils.ZkUtils.ZKConfig;

public class Producer {
	
	private final ReentrantLock mainLock = new ReentrantLock();

	private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, (int) (2 * 2), 50000, TimeUnit.MICROSECONDS,
			new ArrayBlockingQueue<Runnable>(2 * 20), new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					return new Thread("metaq-producer");
				}

			});

	public void run() {
		for (int i = 0; i < 2; i++) {
			threadPool.submit(new Task());
		}
	}

	private class Task implements Runnable {

		@Override
		public void run() {
			sendMessage();

		}

	}

	public void sendMessage() {
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		final MetaClientConfig metaClientConfig = new MetaClientConfig();
		final ZKConfig zkConfig = new ZKConfig();
		zkConfig.zkConnect = "127.0.0.1:2181,127.0.0.1:2181";
		zkConfig.zkRoot = "/meta";
		metaClientConfig.setZkConfig(zkConfig);
		metaClientConfig.setServerUrl("meta://192.168.199.198:8123");
		metaClientConfig.setZkSessionTimeoutMs(60000);
		MessageSessionFactory msFactory = null;
		MessageProducer producer = null;
		try {
			msFactory = new MetaMessageSessionFactory(metaClientConfig);
			producer = msFactory.createProducer();
			// publish topic
			final String topic = "sunmq";
			producer.publish(topic);
			// send message
			int count = 0;
			String msgStr = null;
			while (true && count < 2) {
				msgStr = "billCode: " + (count++);
				Message msg = new Message(topic, msgStr.getBytes("UTF-8"));
				SendResult sendResult = producer.sendMessage(msg);
				// check result
				if (!sendResult.isSuccess()) {
					System.err.println("Send message failed,error message:" + sendResult.getErrorMessage());
				} else {
					System.out.println("Send message successfully,sent to " + sendResult.getPartition());
				}
				Thread.sleep(2000);
			}
		} catch (MetaClientException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			mainLock.unlock();
			try {
				if (producer != null) {
					producer.shutdown();
				}
				if (msFactory != null) {
					msFactory.shutdown();
				}
			} catch (Exception e) {
				//System.exit(1);
			}

		}
	}

	public static void main(String[] args) {
		new Producer().sendMessage();
		new Producer().run();
		try {
			new CountDownLatch(1).await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}*/