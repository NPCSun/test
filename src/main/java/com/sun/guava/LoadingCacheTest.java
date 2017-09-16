package com.sun.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.lang.math.RandomUtils;

/**
 * Created by sun on 2017/8/17 上午9:45.
 */
public class LoadingCacheTest {


	public static void main(String[] args) {
		LoadingCache<String, String> cache = CacheBuilder.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(30, TimeUnit.MINUTES)
				.removalListener(new RemovalListener<String, String>(){
					public void onRemoval(RemovalNotification<String, String> removalNotification) {
						System.out.println("我被移除了。[[");
					}
				})
				.build(
						new CacheLoader<String, String>() {
							//get（key）取不到值时，调用该方法，去数据存在的地方load
							public String load(String key)  {
								return String.valueOf(RandomUtils.nextFloat());
							}
						});
		cache.put("sun", "sun");
		try {
			System.out.println(cache.get("sun"));
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
