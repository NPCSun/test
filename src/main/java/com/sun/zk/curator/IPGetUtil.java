package com.sun.zk.curator;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IPGetUtil {

	public IPGetUtil() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InetAddress ip;
		try {
			// 这种IP容易拿错
			// System.out.println("Current IP address : " +
			// InetAddress.getLocalHost().getHostAddress());
			// 不一定准确的IP拿法
			// 出自比如这篇：http://www.cnblogs.com/zrui-xyu/p/5039551.html
			System.out.println("get LocalHost Address : " + getLocalHostAddress().getHostAddress());

			// 正确的IP拿法
			System.out.println("get LocalHost LAN Address : " + getLocalHostLANAddress().getHostAddress());

		} catch (UnknownHostException e) {

			e.printStackTrace();

		}
	}

	// 正确的IP拿法，即优先拿site-local地址
	static InetAddress getLocalHostLANAddress() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// 遍历所有的网络接口
			for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// 在所有的接口下再遍历IP
				for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
						if (inetAddr.isSiteLocalAddress()) {
							// 如果是site-local地址，就是它了
							return inetAddr;
						} else if (candidateAddress == null) {
							// site-local类型的地址未被发现，先记录候选地址
							candidateAddress = inetAddr;
						}
					}
				}
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

	//出自这篇：http://www.cnblogs.com/zrui-xyu/p/5039551.html
	//实际上的代码是不准的
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