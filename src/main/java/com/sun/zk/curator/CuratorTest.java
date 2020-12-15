/*
package com.sun.zk.curator;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

*/
/**
 * Created by sun on 2018/2/24 上午8:54.
 *//*

public class CuratorTest {
	static CuratorFramework cf = CuratorFrameworkFactory.builder().
			connectString("localhost:2181").
			sessionTimeoutMs(30*1000).
			connectionTimeoutMs(5*1000).
			retryPolicy(new RetryForever(3*1000)).
			build();
	static final String ZK_CLUSTER_PATH = "/CuratorFramework";
	static final ConcurrentMap<String, List<String>> map = new ConcurrentHashMap<>();
	static{
		cf.start();
	}
	public static void register(String nodeName, String ip) throws Exception {
		String path = ZK_CLUSTER_PATH + "/" + nodeName;
		Stat stat = cf.checkExists().forPath(path);
		if (stat == null) {
			// 如果父节点不存在就先创建父节点
			cf.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
		}
		// 创建对应的临时节点
		cf.create().withMode(CreateMode.EPHEMERAL).forPath(path + "/" + ip, ip.getBytes());
	}

	public static void subscribe(String nodeName) throws Exception {
		//订阅某一个服务
		final String path = ZK_CLUSTER_PATH + "/" + nodeName;
		Stat stat = cf.checkExists().forPath(path);
		if (stat == null) {
			// 如果父节点不存在就先创建父节点
			cf.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
		}
		PathChildrenCache cache = new PathChildrenCache(cf, path, true);
		// 在初始化的时候就进行缓存监听
		cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
		cache.getListenable()
				.addListener((CuratorFramework client, PathChildrenCacheEvent event) -> {
					// 重新获取子节点
					List<String> children = client.getChildren().forPath(path);
					// 排序一下子节点
					Collections.sort(children);
					// 子节点重新缓存起来
					map.put(nodeName, children);
					children.forEach(s -> System.out.println(s));
				});
	}

	public static void main(String[] args) {
		CountDownLatch cdl = new CountDownLatch(1);
		try {
			register("sunfeifei", IPGetUtil.getLocalHostLANAddress().getHostAddress());
			//subscribe("sunfeifei");
			cdl.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
*/
