/*
package com.sun.lock;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RLock;
import org.redisson.core.RQueue;

public class RedisDistributedLockTest2 {
	
	private static int count = 0;
	private static long begin;
	private static long end;

	public static void main(String[] args) {
		//
		final CountDownLatch cdl = new CountDownLatch(1);
		final Random random = new Random();
		Config config = new Config();
		config.useSingleServer().setAddress("192.168.199.141:6379");
		final Redisson redisson = Redisson.create(config);
		final String key = "user:002";
		final RLock distributedLock = redisson.getLock(key);
		final RQueue<String> rqueue = redisson.getQueue("anyQueue");
		
		Thread thread = null;
		begin = System.currentTimeMillis();
		Runnable run = new Runnable(){
			public void run (){
				boolean flag = false;
				try {
					while(!flag){
						flag = distributedLock.tryLock(100, TimeUnit.MILLISECONDS);
					}
					count++;
					//
					rqueue.offer(count+"");
					int randCount = 0;
					while(randCount<50){
						randCount = random.nextInt(100);
					}
					randCount = randCount + random.nextInt(50);
					Thread.sleep(100l);
					System.out.println("获取锁：" + key + "\t" + count + "\t" + randCount);
					if(count==100) {
						end = System.currentTimeMillis();
						System.err.println("总耗时（毫秒）：" + (end-begin));
						//
						cdl.countDown();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					distributedLock.unlock();
				}
			}//end for run
		};
		for(int i=0;i<100;i++){
			thread = new Thread(run);
			thread.start();
		}
		*/
/*for(int i=0;i<100;i++){
			System.out.print(rqueue.poll() + "\t");
		}*//*

		System.out.println("");
		try {
			//
			cdl.await();
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			redisson.shutdown();
		}
		
	}
}
*/
