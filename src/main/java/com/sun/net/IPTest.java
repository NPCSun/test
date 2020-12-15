package com.sun.net;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.*;
import java.util.Enumeration;

/**
 * @Deacription TODO
 * @Author sunking
 * @Date 2020/12/15} 8:58 上午
 **/
public class IPTest {

	// Zk客户端
	private CuratorFramework	client;

	// 工作节点的路径
	private String				pathPrefix				= "/product/workIds/";
	private String				ACTUAL_WORK_ID_PREFIX	= "/product/actual-workId/";
	private String				pathRegistered			= null;

	private IPTest() {
		System.setProperty("zookeeper.sasl.client", "false");
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
		client.start();
	}

	// 在zookeeper中创建临时节点并写入信息
	public void create() throws Exception {

		// 创建一个 ZNode 节点
		// 节点的 payload 为当前worker 实例
		pathRegistered = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
				.forPath(pathPrefix);
		System.out.println(pathRegistered);
		String[] segments = pathRegistered.split("/");
		long workId = Long.parseLong(segments[3]) % 32;
		String actualWorkId = ACTUAL_WORK_ID_PREFIX + workId;
		Stat stat = client.checkExists().forPath(actualWorkId);
		if (stat == null) {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(actualWorkId);
		} else {
			System.out.println("重复");
			System.out.println(workId);
			boolean success = false;
			long newWorkId = workId;
			for (int i = 1; i <= 32; i++) {
				newWorkId = newWorkId + 1;
				workId = newWorkId % 32;
				actualWorkId = ACTUAL_WORK_ID_PREFIX + workId;
				stat = client.checkExists().forPath(actualWorkId);
				if (stat == null) {
					client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(actualWorkId);
					success = true;
					break;
				}
			}
			if (!success) {
				throw new RuntimeException("节点超过32个，请核对");
			}
		}
		System.out.println(workId);
	}

	public static void main(String[] args) {
		IPTest ipTest = new IPTest();
		try {
			for (int i = 0; i < 16; i++) {
				ipTest.create();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ipTest.client.close();
		}

		final long workerIdBits = 5;
		// 最大支持机器节点数0~31，一共32个
		// 最大支持数据中心节点数0~31，一共32个
		final long maxWorkerId = -1 ^ (-1 << workerIdBits);
		// System.out.println(maxWorkerId);
		InetAddress ip;
		try {
			// 这种IP容易拿错
			// System.out.println("Current IP address : " +
			// InetAddress.getLocalHost().getHostAddress());
			// 不一定准确的IP拿法
			// 出自比如这篇：http://www.cnblogs.com/zrui-xyu/p/5039551.html
			// System.out.println("get LocalHost Address : " +
			// getLocalHostAddress().getHostAddress());

			// 正确的IP拿法
			System.out.println("get LocalHost LAN Address : " + getLocalHostLANAddress().getHostAddress());

		} catch (UnknownHostException e) {

			e.printStackTrace();

		}
	}

	// 正确的IP拿法，即优先拿site-local地址
	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
			while (ifaces.hasMoreElements()) {
				NetworkInterface networkInterface = ifaces.nextElement();
				// System.out.println(networkInterface.getName());
				if ("en0".equalsIgnoreCase(networkInterface.getName())) {
					Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress add = addresses.nextElement();
						if (!add.isLoopbackAddress() && add.isSiteLocalAddress()) {
							String siteLocalAddress = add.getHostAddress();
							System.out.println(add.getHostAddress());
							String[] segmenets = siteLocalAddress.split("\\.");
							System.out.println(segmenets[3]);
							System.out.println(Long.parseLong(segmenets[3]) % 32);
							SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(
									Long.parseLong(segmenets[3]) % 32, 0);
						}
					}
				}
				System.out.println("------------------------");
			}
			if (candidateAddress != null) {
				return candidateAddress;
			}
			// 如果没有发现 non-loopback地址.只能用最次选的方案
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException(
					"Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

	// 出自这篇：http://www.cnblogs.com/zrui-xyu/p/5039551.html
	// 实际上的代码是不准的
	private static InetAddress getLocalHostAddress() throws UnknownHostException {
		Enumeration allNetInterfaces;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();

				Enumeration addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
						if (ip != null && ip instanceof Inet4Address) {
							return ip;
						}
					}
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
		if (jdkSuppliedAddress == null) {
			throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
		}
		return jdkSuppliedAddress;
	}

}
