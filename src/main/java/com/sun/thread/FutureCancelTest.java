package com.sun.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureCancelTest {
	
	static void cancelSleepFuture() throws InterruptedException{
		ExecutorService ext=Executors.newFixedThreadPool(1);
        Future<String> future3=ext.submit(new Callable<String>(){
            public String call(){
                int i=0;
                while(i<10){
                    System.out.println("callable3 running......");
                    try {
                        Thread.sleep(3000);
                        i++;
                    } catch (InterruptedException e) {
                         System.out.println("Interrupted task."); 
                         throw new RuntimeException("异常退出中断");
                    }
                    
                }
                return "callable3 running";        
            }
        });
		Thread.sleep(100);
        System.out.println("callable3 cancel=" + future3.cancel(true));
        System.out.println("callable3 over");
        ext.shutdown();
	}
	
	static void cancelCommonFuture() throws InterruptedException{
		ExecutorService ext=Executors.newFixedThreadPool(1);
        Future<String> future3=ext.submit(new Callable<String>(){
            public String call(){
                System.out.println(Thread.currentThread().getName());
                System.out.println();
                while(!Thread.currentThread().isInterrupted()){
                    System.out.println("commonFuture running......");
                }
                return "callable3 running";        
            }
        });
		Thread.sleep(100);
        System.out.println("callable3 cancel=" + future3.cancel(true));
        System.out.println("callable3 over");
        ext.shutdown();
	}
	
	public static void main(String[] args) throws InterruptedException  {
		//cancelCommonFuture();
		cancelSleepFuture();
	}
}
