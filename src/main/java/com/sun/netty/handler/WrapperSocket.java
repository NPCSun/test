package com.sun.netty.handler;

import java.io.IOException;
import java.net.Socket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;

public class WrapperSocket {

	public static void main(String[] args) throws IOException, Exception {
		// oio 
		Socket mySocket = new Socket("www.baidu.com", 80); //netty
		SocketChannel ch = new OioSocketChannel(mySocket); 
		EventLoopGroup group = new OioEventLoopGroup(); 
		//register channel
		ChannelFuture registerFuture = group.register(ch); 
		//de-register channel
		ChannelFuture deregisterFuture = ch.deregister();
		
		

	}

}
