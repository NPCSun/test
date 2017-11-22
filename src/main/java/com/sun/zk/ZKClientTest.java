package com.sun.zk;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;

/**
 * Created by sun on 2017/11/22 上午10:56.zkclient
 */
public class ZKClientTest {
	public static void main(String[] args) {
		ZkClient zkClient = new ZkClient("10.0.0.230:2181");

		zkClient.deleteRecursive("/test");
		zkClient.createPersistent("/test");

		for (int i=0; i<300; i++) {
			zkClient.createPersistentSequential("/test/sun",true);
		}

		List<String> children = zkClient.getChildren("/test");
		System.out.println(children.stream().count());
	}
}
