/*package com.sun.cache;

import java.io.Serializable;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;  
  
public class MemcachedCacheTest {  
	
	public static class Cache implements Serializable{
		private String key;
		private String value;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
	}
    public static void main(String[] args) {  
        MemCachedClient client=new MemCachedClient();  
        String [] addr ={"192.168.199.141:11211"};  
        Integer [] weights = {3};  
        SockIOPool pool = SockIOPool.getInstance();  
        pool.setServers(addr);  
        pool.setWeights(weights);  
        pool.setInitConn(5);  
        pool.setMinConn(5);  
        pool.setMaxConn(200);  
        pool.setMaxIdle(1000*30*30);  
        pool.setMaintSleep(30);  
        pool.setNagle(false);  
        pool.setSocketTO(30);  
        pool.setSocketConnectTO(0);  
        pool.initialize();  
        
        Cache cache =  new Cache();
        cache.setKey("memcached");
        cache.setValue("slfalfahsdf;ahsl");
        client.set("memcached", cache); 
          
        cache = (Cache) client.get("memcached");
        client.flushAll();
        System.out.println(cache.getKey());  
    }  
}  */