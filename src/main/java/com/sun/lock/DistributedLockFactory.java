/**
 * mario.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.sun.lock;


/**
 * apache zookeeper lock
 * @author liming
 * @version $Id: DistributedLockFactory.java, v 0.1 2016年1月8日 下午5:19:56 liming Exp $
 */
public class DistributedLockFactory {
	
	private String serverIp ;

	public DistributedLock getLock(String lockName){
		return new DistributedLock(lockName, serverIp);
	}

	/**
	 * Getter method for property <tt>serverIp</tt>.
	 * 
	 * @return property value of serverIp
	 */
	public String getServerIp() {
		return serverIp;
	}

	/**
	 * Setter method for property <tt>serverIp</tt>.
	 * 
	 * @param serverIp value to be assigned to property serverIp
	 */
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	
}
