package com.sun.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by sun on 2018/1/9 上午11:47.
 */
public class ObjectPoolTest {
	public static void main(String[] args) {
		GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
		conf.setMaxTotal(20);
		conf.setMaxIdle(10);
		//GenericObjectPool<StringBuffer> pool = new GenericObjectPool<StringBuffer>(new StringBufferFactory(), conf);
	}
}
