package com.sun.netty.main;

import com.sun.netty.handler.EchoServerHandler;
import com.sun.netty.handler.LoggerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;

public class NettyServerMain {

	private static final int port = 9000;

	private static final ConcurrentMap<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>(1000);

	private class NameThreadFactory implements ThreadFactory {

		private int counter = 1;
		private String prefix = "";

		public NameThreadFactory(String prefix) {
			this.prefix = prefix;
		}

		public Thread newThread(Runnable r) {
			return new Thread(r, prefix + "-" + counter++);
		}
	}

	public void start() throws InterruptedException {
		//System.getProperties().put("io.netty.noKeySetOptimization", true);
		ServerBootstrap serverBootstrap = new ServerBootstrap();// 引导辅助程序
		NioEventLoopGroup main = new NioEventLoopGroup(2, new NameThreadFactory("server-nio-main"));// 通过nio方式来接收连接和处理连接
		//acceptors.setIoRatio(70);
		NioEventLoopGroup workers = new NioEventLoopGroup(2, new NameThreadFactory("server-nio-workers"));// 通过nio方式来接收连接和处理连接
		try {
			serverBootstrap.localAddress(new InetSocketAddress(port));// 设置监听端口
			serverBootstrap.group(main, workers);
			serverBootstrap.channel(NioServerSocketChannel.class);// 设置nio类型的channel
			serverBootstrap.handler(new LoggerHandler());
			//
			ChannelHandler childHandler = new ChannelInitializer<SocketChannel>() {// 有连接到达时会创建一个channel
				protected void initChannel(SocketChannel ch) throws Exception {
					// new DelimiterBasedFrameDecoder(8192,
					// Delimiters.lineDelimiter());
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
					ch.pipeline().addLast(new StringDecoder());
					final EventLoopGroup trafficShapingers = new NioEventLoopGroup(2,
							new NameThreadFactory("server-traffic-shaping"));
					// GlobalTrafficShapingHandler
					//GlobalTrafficShapingHandler gtshandler = new GlobalTrafficShapingHandler(trafficShapingers, 1000);
					//ch.pipeline().addLast("trafficshapingHandler", gtshandler);
					// pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
					ch.pipeline().addLast("myHandler", new EchoServerHandler(channelMap));
				}
			};
			serverBootstrap.childHandler(childHandler);
			serverBootstrap.option(ChannelOption.SO_BACKLOG, 128) // (5)
					.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

			ChannelFuture f = serverBootstrap.bind().sync();// 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
			System.out
					.println(NettyServerMain.class.getName() + " started and listen on " + f.channel().localAddress());
			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully shut down your server.
			f.channel().closeFuture().sync();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// It returns a Future that notifies you when the EventLoopGroup has
			// been terminated completely
			// and all Channels that belong to the group have been closed.
			// 关闭EventLoopGroup，释放掉所有资源包括创建的线程
			main.shutdownGracefully().sync();
			workers.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) {
		try {
			new NettyServerMain().start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}