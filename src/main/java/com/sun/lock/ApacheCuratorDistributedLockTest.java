package com.sun.lock;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * 注意：netflix-curator 与apache-curator并存之后会有依赖包冲突。
 * @author sun
 *
 */
public class ApacheCuratorDistributedLockTest {

	public  static void test3() throws Exception{
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		client.start();
		String lockName = "/lock2";
		final InterProcessLock distributedLock = new InterProcessMutex(client, lockName);
		long curr = System.currentTimeMillis();
		for(int i=0;i<1000;i++){
			boolean flag = distributedLock.acquire(1000, TimeUnit.MICROSECONDS);
			//System.out.println(flag);
			distributedLock.release();
		}
		System.out.println(System.currentTimeMillis() - curr);
		client.close();
	}
	
	//String lockName = "/lock100000";
	public static void test(String lockName){
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		long begin = System.currentTimeMillis();
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		client.start();
		long end = System.currentTimeMillis();
		System.out.println("初始化client耗时：" + (end-begin));
		begin = end;
		InterProcessLock lock = null;
		try {
			lock = new InterProcessMutex(client, lockName);
			end = System.currentTimeMillis();
			System.out.println("初始化InterProcessMutex耗时：" + (end-begin));
			begin = end;
			for(int i=0;i<10000;i++){
				boolean flag = lock.acquire(1000, TimeUnit.MICROSECONDS);
				//System.out.println("获取锁成功：" + flag);
				lock.release();
			}
			end = System.currentTimeMillis();
			System.out.println("锁竞争&执行耗时:" + (end-begin));
		} catch (Exception e) {
			System.out.println("获取锁异常");
		}finally{
			try {
				if( client != null){
					client.close();
				}
				System.out.println("释放锁");
			} catch (Exception e) {
				System.out.println("释放锁异常");
			}
		}
	}
	
	private static int count = 0;
	private static int success_count = 0;
	private static long begin;
	private static long end;

	public static void test1(){
		//
		final CountDownLatch cdl = new CountDownLatch(1);
		final Random random = new Random();
		/*RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 0);
		//CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		CuratorFramework client = CuratorFrameworkFactory.builder()//.namespace("namespace1")
				.connectString("192.168.199.141:2181")
				.retryPolicy(new RetryNTimes(1, 1000))
				.connectionTimeoutMs(5000).build();
		client.start();*/
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		client.start();
		//
		final String lockName = "/lock1";
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
		for(int i=0;i<10;i++){
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
	
	public static void testTPS(){
		//
		final CountDownLatch cdl = new CountDownLatch(1);
		final Random random = new Random();
		/*RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 0);
		//CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		CuratorFramework client = CuratorFrameworkFactory.builder()//.namespace("namespace1")
				.connectString("192.168.199.141:2181")
				.retryPolicy(new RetryNTimes(1, 1000))
				.connectionTimeoutMs(5000).build();
		client.start();*/
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		client.start();
		//
		final String lockName = "/lock1";
		final CuratorFramework paramCurator = client;
		Thread thread = null;
		begin = System.currentTimeMillis();
		for(int i=0;i<100;i++){
			thread = new Thread(new Runnable(){
				public void run (){
					boolean flag = false;
					final InterProcessLock distributedLock = new InterProcessMutex(paramCurator, lockName+System.currentTimeMillis());
					try {
						flag = distributedLock.acquire(10000, TimeUnit.MILLISECONDS);
						System.out.println(flag);
						count++;
						if(count==100) {
							end = System.currentTimeMillis();
							System.err.println("总耗时（毫秒）：" + (end-begin));
							System.out.println("成功获取锁个数" + (success_count+1));
							cdl.countDown();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						try {
							if(flag==true){
								distributedLock.release();
								System.out.println("lock.release() success!");
								success_count++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}//end for run
			});
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
	
	public static void main(String[] args) throws Exception {
		testTPS();
		/*RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.199.141:2181", 30000, 5000, retryPolicy);
		client.start();
		String lockName = "/lock10";
		InterProcessLock lock1 = new InterProcessMutex(client, lockName);
		InterProcessLock lock2 = new InterProcessMutex(client, lockName);
		lock1.acquire();
		boolean result = lock2.acquire(1, TimeUnit.SECONDS);
		System.out.println(result);
		lock1.release();
		result = lock2.acquire(1, TimeUnit.SECONDS);
		System.out.println(result);
		client.close();*/
	}

}
