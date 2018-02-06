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
public class UdpClient {
	public static void main(String[] args) throws Exception {
		try{
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
			//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			/*while ((msg = reader.readLine()) != "") {
				//发送数据
				byte[] buffer = msg.getBytes();
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 8080);
				datagramSocket.send(packet);
				System.out.println("完成发送！");

				//接收数据
				DatagramPacket inputPacket = new DatagramPacket(new byte[512], 512);
				//datagramSocket.receive(inputPacket);
				System.out.println(new String(inputPacket.getData(), 0, inputPacket.getLength()));
				//datagramSocket.close();
			}*/

		/*while (true) {
			//接收数据
			DatagramPacket inputPacket = new DatagramPacket(new byte[512], 512);
			datagramSocket.receive(inputPacket);
			System.out.println(new String(inputPacket.getData(), 0, inputPacket.getLength()));
			//datagramSocket.close();
		}*/

		while (true) {
			byte[] buffer = "hello".getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 8080);
			datagramSocket.send(packet);
			Thread.sleep(1);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
