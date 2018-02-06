package com.sun.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by sun on 2018/2/2 上午10:06.
 */
public class UDPServer {
	public static void main(String[] args) throws Exception {
		DatagramSocket datagramSocket = new DatagramSocket(8082);
		DatagramPacket packet;
		while (true) {
			packet = new DatagramPacket(new byte[512], 512);
			datagramSocket.receive(packet);
			System.out.println(new String(packet.getData(), 0, packet.getLength()));
			packet.setData("I am server!!!".getBytes());
			packet.setSocketAddress(new InetSocketAddress("255.255.255.255", 55555));
			datagramSocket.send(packet);
			Thread.sleep(2000);
		}
	}
}
