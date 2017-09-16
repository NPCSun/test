package com.sun.lock;



import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;


/**
 * 
 * @author liming
 * @version $Id: DistributedLock.java, v 0.1 2016年1月8日 下午5:19:44 liming Exp $
 */
public class DistributedLock {
	
	private CuratorFramework client;
	
	private String lockPath = "/webserver/locks";
	
	private InterProcessMutex disLock;
	
	public DistributedLock(String lockName,String serverIp){
		this.client = CuratorFrameworkFactory.newClient(serverIp,10000,10000,new ExponentialBackoffRetry(1000, 3));
		this.client.start();
		lockPath = lockPath + File.separator + lockName;
		disLock = new InterProcessMutex(client,lockPath);
	}
	
	public boolean lock(long timeout){
		try {
			return this.disLock.acquire(timeout, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean lock(){
		try {
			return this.disLock.acquire(1,TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void unlock(){
		try {
			this.disLock.release();
			CloseableUtils.closeQuietly(this.client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
