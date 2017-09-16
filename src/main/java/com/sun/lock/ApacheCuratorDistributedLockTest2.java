package com.sun.lock;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;

/**
 * 注意：netflix-curator 与apache-curator并存之后会有依赖包冲突。
 * @author sun
 *
 */
public class ApacheCuratorDistributedLockTest2 {

	public static void test(){
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		client.start();
		String lockName = "/lock2";
		InterProcessLock lock = new InterProcessMutex(client, lockName);
		try {
			System.out.println("获取锁");
			boolean flag = lock.acquire(1000, TimeUnit.MICROSECONDS);
			System.out.println("获取锁成功：" + flag);
		} catch (Exception e) {
			System.out.println("获取锁异常");
		}finally{
			try {
				lock.release();
				System.out.println("释放锁");
			} catch (Exception e) {
				System.out.println("释放锁异常");
			}
		}
	}
	
	private static int count = 0;
	private static long begin;
	private static long end;

	public static void test1(){
		//
		final CountDownLatch cdl = new CountDownLatch(1);
		final Random random = new Random();
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 0);
		//CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		CuratorFramework client = CuratorFrameworkFactory.builder()//.namespace("namespace1")
				.connectString("192.168.199.141:2181")
				.retryPolicy(new RetryNTimes(1, 1000))
				.connectionTimeoutMs(5000).build();
		client.start();
		//
		final String lockName = "/lock2";
		final CuratorFramework paramCurator = client;
		Thread thread = null;
		begin = System.currentTimeMillis();
		Runnable run = new Runnable(){
			public void run (){
				final InterProcessLock distributedLock = new InterProcessMutex(paramCurator, lockName);
				boolean flag = false;
				try {
					while(!flag){
						flag = distributedLock.acquire(100, TimeUnit.MILLISECONDS);
					}
					count++;
					//
					int randValue = random.nextInt(100);
					Thread.sleep(100);
					System.out.println("获取锁：" + lockName + "\t" + count + "\t" + randValue );
					if(count==100) {
						end = System.currentTimeMillis();
						System.out.println("\r\n");
						System.err.println("总耗时（毫秒）：" + (end-begin));
						//
						cdl.countDown();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					try {
						distributedLock.release();
					} catch (Exception e) {
						System.out.println("lock.release()异常" + count);
					}
				}
			}//end for run
		};
		for(int i=0;i<100;i++){
			thread = new Thread(run);
			thread.start();
		}
		try {
			//
			cdl.await();
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			client.close();
		}
	}
	public static void test2(){
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		client.start();
		try {
			String result = null;
			Stat stat = client.checkExists().forPath("/sun");
			if(stat == null){
				System.out.println("path /sun is not exists.");
				result = client.create().forPath("/sun", "hi apache curator".getBytes());
				System.out.println(result);
			}else{
				System.out.println("path /sun have existed.");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	public static void main(String[] args) {
		//
		test1();
		
	}

}
