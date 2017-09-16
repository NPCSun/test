/*package com.sun.netty.main;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;

import com.sun.mq.Producer;
import com.sun.netty.handler.EchoClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {
	private final String host;
	private final int port;

	public EchoClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start1() {
		// jboss(netty3) api
		ClientBootstrap cbs = new ClientBootstrap();
	}

	public void start() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group);
			b.channel(NioSocketChannel.class);
			b.remoteAddress(new InetSocketAddress(host, port));
			b.handler(new ChannelInitializer<SocketChannel>() {

				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new EchoClientHandler());
				}
			});
			ChannelFuture f = b.connect().sync();

			
			 * f.addListener(new ChannelFutureListener() { public void
			 * operationComplete(ChannelFuture future) throws Exception {
			 * if(future.isSuccess()){ System.out.println("client connected");
			 * }else{ System.out.println("server attemp failed");
			 * future.cause().printStackTrace(); } } });
			 

			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				group.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new EchoClient("127.0.0.1", 9000).start();
	}
}*/