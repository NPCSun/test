package com.sun.thread;

/**
 * Created by sun on 2017/12/22 上午10:17.
 */
public class WaitTest {

	private volatile String result;

	public void get(int n){
		if(result != null){
			return ;
		}

		synchronized (this){
			try {
				wait();
				System.out.println("我是第\t" + n);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void setResult(String result){
		this.result = result;
		//notify();	    //dangerous
		notifyAll();	//safe
	}


	public static void main(String[] args) {
		WaitTest wt  = new WaitTest();
		int count = 0;
		Thread thread;
		while(count<5){
			int finalCount = count;
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					wt.get(finalCount);
				}
			});
			thread.start();
			count = count +1 ;
		}
		count = 0;

		while(count<5){
			int finalCount = count;
			new Thread(new Runnable() {
				@Override
				public void run() {
					wt.setResult("success");
				}
			}).start();
			count = count +1 ;
		}
	}
}
