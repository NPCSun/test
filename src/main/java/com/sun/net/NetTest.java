package com.sun.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by sun on 2017/11/22 下午2:43.
 */
public class NetTest {
	public static void main(String[] args) {
		try {
			String host = InetAddress.getLocalHost().getHostAddress();
			System.out.println(host);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
