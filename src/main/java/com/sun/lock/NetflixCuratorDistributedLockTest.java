/*package com.sun.lock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.locks.InterProcessLock;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import com.netflix.curator.retry.RetryNTimes;

public class NetflixCuratorDistributedLockTest {
	

	public static void main(String[] args) {
		final CountDownLatch cdl = new CountDownLatch(1);
		CuratorFramework curator = null;
		String path = "/test_path"; 
		try {
			curator = CuratorFrameworkFactory.builder()
					.connectString("192.168.199.141:2181")
					.retryPolicy(new RetryNTimes(5, 1000))
					.connectionTimeoutMs(5000).build();
			curator.start();
//			System.out.println(curator.create().forPath("/test/head", "the data of head".getBytes()));
			final String lockName = "/lock1";
			final CuratorFramework paramCurator = curator;
			for(int i=0;i<10;i++){
				final int count = i;
				Runnable run = new Runnable(){

					@Override
					public void run() {
						InterProcessLock lock = new InterProcessMutex(paramCurator, lockName);
						boolean result;
						try {
							result = lock.acquire(3000, TimeUnit.MILLISECONDS);
							if(result){
								Thread.sleep(500);
							}
							System.out.println(result);
						} catch (Exception e) {
							System.out.println("lock.acquire()异常" + count);
						}finally{
							if(count==9){
								cdl.countDown();
							}
							try {
								lock.release();
							} catch (Exception e) {
								System.out.println("lock.release()异常" + count);
							}
						}
					}
					
				};
				Thread t = new Thread(run);
				t.start();
			}
		} catch (IOException e2) {
			System.out.println("curator初始化错误！");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}finally{
			if(curator != null){
				try {
					cdl.await();
					System.out.println("关闭curator，结束。");
				} catch (InterruptedException e) {
					System.out.println("cdl异常");
				}
				curator.close();
			}
		}
		
		System.exit(0);

	}

}
*/