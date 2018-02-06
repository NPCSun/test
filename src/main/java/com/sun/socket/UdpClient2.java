package com.sun.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by sun on 2018/2/2 上午10:04.
 */
public class UdpClient2 {
	public static void main(String[] args) throws Exception {
		DatagramSocket datagramSocket = new DatagramSocket(null);
		datagramSocket.setReuseAddress(true);
		/*if(datagramSocket.getReuseAddress()){
			datagramSocket.setReuseAddress(true);
		}else{
			System.err.println("ReuseAddress is not supported!");
		}*/
		datagramSocket.bind(new InetSocketAddress(55555));
		InetAddress address = InetAddress.getByName("localhost");
		String msg;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			//接收数据
			DatagramPacket inputPacket = new DatagramPacket(new byte[512], 512);
			datagramSocket.receive(inputPacket);
			System.out.println(new String(inputPacket.getData(), 0, inputPacket.getLength()));
			//datagramSocket.close();
		}

	}

}
