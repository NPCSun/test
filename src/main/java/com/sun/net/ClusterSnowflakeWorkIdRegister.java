package com.sun.net;

import java.net.*;
import java.util.Enumeration;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * 集群环境下，雪花生成器workId注册器
 * @author sunking
 * @date 2020/12/16 6:21 下午
 **/
public class ClusterSnowflakeWorkIdRegister {

	// Zk客户端
	private CuratorFramework	client;

	// 工作节点的路径
	private String				WORK_IDS_NODE	= "/product/workIds";
	private String				IP_WORK_ID_PREFIX			= "/product/ip-workId/";
	private String				WORK_ID_IPPREFIX			= "/product/workId-ip/";
	private String				pathRegistered				= null;

	public long registerWorkId() throws Exception {
		Long workId;
		// 检查过去是否成功注册过
		String siteLocalIP = getLocalHostIP();
		Stat stat = client.checkExists().forPath(IP_WORK_ID_PREFIX + siteLocalIP);
		if (stat != null) {
			byte[] workIdData = client.getData().forPath(IP_WORK_ID_PREFIX + siteLocalIP);
			workId = Long.parseLong(new String(workIdData, "utf-8"));
			if (siteLocalIP.equals(siteLocalIP)) {
				System.out.println("过去注册过，可以复用。workId is:\t" + workId);
				return workId;
			}
		} else {
			// 检查节点是否存在，如果不存在，则创建
			stat = client.checkExists().forPath(WORK_IDS_NODE);
			if (stat == null) {
				pathRegistered = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
						.forPath(WORK_IDS_NODE);
			}
			pathRegistered = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
					.forPath(WORK_IDS_NODE + "/");
			String[] segments = pathRegistered.split("/");
			workId = Long.parseLong(segments[3]) % 32;
			String workIdStr = String.valueOf(workId);
			// 查看workId是否被注册过
			stat = client.checkExists().forPath(WORK_ID_IPPREFIX + workId);
			if (stat == null) {
				// 持久化ip、workId映射关系
				client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
						.forPath(WORK_ID_IPPREFIX + workIdStr, siteLocalIP.getBytes("utf-8"));
				client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
						.forPath(IP_WORK_ID_PREFIX + siteLocalIP, workIdStr.getBytes("utf-8"));
			} else {
				boolean success = false;
				for (int i = 0; i < 32; i++) {
					workId = (long) i;
					stat = client.checkExists().forPath(WORK_ID_IPPREFIX + workId);
					if (stat == null) {
						// 持久化ip、workId映射关系
						client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
								.forPath(WORK_ID_IPPREFIX + workIdStr, siteLocalIP.getBytes("utf-8"));
						client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
								.forPath(IP_WORK_ID_PREFIX + siteLocalIP, workIdStr.getBytes("utf-8"));
						success = true;
						break;
					}
				}
				if (!success) {
					throw new RuntimeException("节点数超过32个，请联系技术/运维核对。");
				}
			}
		}
		return workId;
	}

	private ClusterSnowflakeWorkIdRegister() {
		System.setProperty("zookeeper.sasl.client", "false");
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000, 2);
		client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
		client.start();
		try {
			client.blockUntilConnected();
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

	private static String getLocalHostIP() throws UnknownHostException {
		try {
			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
			while (ifaces.hasMoreElements()) {
				NetworkInterface networkInterface = ifaces.nextElement();
				if ("en0".equalsIgnoreCase(networkInterface.getName())) {
					Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress inetAddress = addresses.nextElement();
						if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
							return inetAddress.getHostAddress();
						}
					}
				}
			}
			InetAddress localInetAddress = InetAddress.getLocalHost();
			if (localInetAddress == null) {
				throw new RuntimeException("获取本地ip失败");
			}
			return localInetAddress.getHostAddress();
		} catch (Exception e) {
			throw new RuntimeException("获取本地ip失败");
		}
	}

	public static void main(String[] args) {
		ClusterSnowflakeWorkIdRegister clusterSnowflakeWorkIdRegister = new ClusterSnowflakeWorkIdRegister();
		try {
			for (int i = 0; i < 64; i++) {
				clusterSnowflakeWorkIdRegister.registerWorkId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clusterSnowflakeWorkIdRegister.client.close();
		}
	}

}
