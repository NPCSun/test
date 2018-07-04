package com.sun.pool;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by sun on 2018/1/9 上午11:47.
 */
public class ObjectPoolTest {
	public static void main(String[] args) {
		GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
		conf.setMaxTotal(10);
		conf.setMaxIdle(10);
		conf.setMaxWaitMillis(5000);
		GenericObjectPool<String> pool = new GenericObjectPool<String>(new StringFactory(), conf);
		String str = null;
		Timer timer = new Timer();
		for(int i=0;i<1500;i++){
			System.out.println(i+":");
			try {
				str = pool.borrowObject();
				System.out.println(str);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(str != null){
					final String objToReturn = str;
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							pool.returnObject(objToReturn);
						}
					}, 10);
				}
			}
		}//end for
		timer.cancel();
		pool.clear();
		pool.close();
	}


}
