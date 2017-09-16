package com.sun.lock;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * 
 *
 */
public class RedisTranctionTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		Jedis jedis = new Jedis("192.168.199.141", 6379, 30000);
		jedis.set("count", "1");
		jedis.incr("count");
		System.out.println("count:" + jedis.get("count"));
		byte[] result = jedis.get("count".getBytes("UTF-8"));
		System.out.println("count:" + new String(result, "UTF-8").toString());
		Transaction tx = jedis.multi();
		System.out.println(tx.set("redis", new Date().toString()));
		//
		System.out.println("tx.get():" + tx.get("redis"));
		List<Object> list = tx.exec();
		System.out.println("----------");
		String strr = null;
		for(Object o :list){
			strr = (String) o;
			System.out.println(strr);
		}
		//System.out.println(jedis.get("redis"));
		jedis.close();

	}

}
