package com.sun.lock;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

public class Test {

	
	private final ReentrantLock mainLock = new ReentrantLock();
	
	private final ThreadPoolExecutor pool = null;


	public void sendMessage() {
		final ReentrantLock mainLock = this.mainLock;
		mainLock.lock();
		try{
			
		}finally{
			mainLock.unlock();
		}
	
	}
	
	public static void main(String[] args) {
		Test t = new Test();
		t.sendMessage();

	}

}
