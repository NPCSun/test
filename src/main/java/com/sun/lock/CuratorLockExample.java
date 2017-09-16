/*package com.sun.lock;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

*//**
 *	目前测试未出现问题。
 *
 *//*
public class CuratorLockExample {
	private static final int QTY = 5;
	private static final int REPETITIONS = QTY * 10;
	private static final String PATH = "/locks";

	public static void testTPS() throws InterruptedException{
		ExecutorService service = Executors.newFixedThreadPool(QTY);
		try {
			final long start = System.currentTimeMillis();
			for (int i = 0; i < 100; ++i) {
				//Thread.sleep(1000);
				final int count = i;
				Callable<Void> task = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						//long begin = System.currentTimeMillis();
						CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", new ExponentialBackoffRetry(1000, 3));
						String dataNode = PATH;
						try {
							client.start();
							//long end = System.currentTimeMillis();
							//System.out.println("client start 耗時:" + (end-begin));
							InterProcessMutex disLock = new InterProcessMutex(client, dataNode);
							for (int j = 0; j < 1; ++j) {
								long begin = System.currentTimeMillis();
								boolean flag = disLock.acquire(10000, TimeUnit.MILLISECONDS);
								//Thread.sleep(50);
								//disLock.acquire();
								long end = System.currentTimeMillis();
								System.out.println(end-begin);
								if(!flag)
									System.out.println(flag);
								if(flag == true){
									disLock.release();
								}
								if(count==99){
									end = System.currentTimeMillis();
									System.out.println("耗时：" + (end-start));
								}
							}
						} catch (Throwable e) {
							e.printStackTrace();
						} finally {
							try{
								client.delete().forPath(dataNode);
							}catch(Exception e){
								e.printStackTrace();
							}
							CloseableUtils.closeQuietly(client);
						}
						return null;
					}
				};
				service.submit(task);
			}
			service.shutdown();
			service.awaitTermination(10, TimeUnit.MINUTES);
			System.out.println("-------The end.-------");
		} finally {
			//
		}
	}
	public static void main(String[] args) throws Exception {
		testTPS();
		for (int i = 0; i < 50; ++i) {
			testTPS();
			Thread.sleep(1000);
		}
		
	}
}*/