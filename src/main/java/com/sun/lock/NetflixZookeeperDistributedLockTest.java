/*package com.sun.lock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.locks.InterProcessLock;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import com.netflix.curator.retry.RetryNTimes;

public class NetflixZookeeperDistributedLockTest {
	
	private static int count = 0;
	private static long begin;
	private static long end;

	public static void main(String[] args) {
		//
		final CountDownLatch cdl = new CountDownLatch(1);
		
		CuratorFramework curator = null;
		String path = "/test_path"; 
		try {
			curator = CuratorFrameworkFactory.builder()
					.connectString("192.168.199.141:2181")
					.retryPolicy(new RetryNTimes(1, 1000))
					.connectionTimeoutMs(5000).build();
		} catch (IOException e1) {
			System.out.println("curator初始化错误！");
		}
		curator.start();
		final String lockName = "/lock1";
		final CuratorFramework paramCurator = curator;
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
					Thread.sleep(100l);
					System.out.println("获取锁：" + lockName + "\t" + count);
					if(count==100) {
						end = System.currentTimeMillis();
						System.err.println("总耗时（毫秒）：" + (end-begin));
						//
						cdl.countDown();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
		for(int i=0;i<100;i++){
			System.out.print(rqueue.poll() + "\t");
		}
		System.out.println("");
		try {
			//
			cdl.await();
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			curator.close();
		}
		
	}
}
*/