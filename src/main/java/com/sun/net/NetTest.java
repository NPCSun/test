package com.sun.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by sun on 2017/11/22 下午2:43.
 */
public class NetTest {
	public static void main(String[] args) {
		try {
			InetAddress hostInet = InetAddress.getLocalHost();
			String host = hostInet.getHostAddress();
			System.out.println(host);
			InetAddress[] inetAddresses = InetAddress.getAllByName("sundeMacBook-Air.local");
			for(InetAddress item : inetAddresses){
				System.out.println(item.getHostAddress());
			}
			//System.out.println(host);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
