package com.sun.netty.self;

import java.util.concurrent.ThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by sun on 2017/9/26 上午11:24.
 */
public class NettyServer {

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

	public void bind(int port) throws InterruptedException {

		EventLoopGroup bossGroup = new NioEventLoopGroup(1, new NameThreadFactory("server-nio-main"));
		EventLoopGroup workerGroup = new NioEventLoopGroup(1, new NameThreadFactory("server-nio-worker"));
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					.option(ChannelOption.SO_BACKLOG, 128)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {

							ChannelPipeline p = ch.pipeline();
							p.addLast(new MessageDecoder(1<<20, 10, 4));
							p.addLast(new MessageEncoder());
							p.addLast(new ServerHandler());
						}
					});

			// Bind rand start to accept incoming connections.
			ChannelFuture future = b.bind(port).sync(); // (7)

			// Wait until the server socket is closed.
			future.channel().closeFuture().sync();

		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class TestHandler extends ChannelDuplexHandler {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ctx.fireChannelRead(msg);
		}
	}

	private class ServerHandler extends SimpleChannelInboundHandler<Message> {

		private long begin = System.currentTimeMillis();

		private int count = 0;

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
			/*if(msg.getType() == 0){
				System.out.println("心跳包 " + count);
				count++;
			}else{
				Message resp = new Message((byte) 1, "Hello world from server");
				ctx.writeAndFlush(msg);
			}*/
			if(count <1){
				Message resp = new Message((byte) 1, "Hello world from server");
				ctx.writeAndFlush(msg);
			}
			if(count%100000==0){
				long end = System.currentTimeMillis();
				System.out.println("收到 100000条 msg. 耗时(毫秒)：" + (end-begin));
				begin = end;
			}
			count++;

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}

	public static void main(String[] args) throws Exception {

		new NettyServer().bind(8081);
	}
}