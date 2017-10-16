package com.sun.netty.self;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by sun on 2017/9/27 上午9:54.
 */
public class NettyClient {
	public void connect(String host, int port) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024))//1KB=1024*8byte=8192byte
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {

							ChannelPipeline p = ch.pipeline();
							p.addLast(new ClientMessageDecoder(1<<20, 0, 4));
							p.addLast(new MessageEncoder());

							p.addLast(new ClientHandler());
						}
					});

			ChannelFuture future = b.connect(host, port).sync();

			future.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);

			future.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	private class ClientHandler extends SimpleChannelInboundHandler<Message> {

		private int count = 0;

		private long begin = System.currentTimeMillis();

		String body = "Hello world from client:";
		Message msg = new Message((byte)1, body);

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			ctx.writeAndFlush(msg);
			ctx.executor().schedule(new HeartBeatTask(ctx), 5, TimeUnit.SECONDS);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
			/*ctx.writeAndFlush(msg);
			count++;
			if(count%50000 == 0){
				long end = System.currentTimeMillis();
				System.out.println("client read 50000 msg. 耗时(毫秒)：" + (end-begin));
				begin = end;
			}*/
			Channel channel = ctx.channel();
			while(true){
				//Thread.sleep(10);

				if(channel.isWritable()){
					ctx.writeAndFlush(msg);
				}

			}

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}

	public static void main(String[] args) throws Exception {

		new NettyClient().connect("127.0.0.1", 8081);
	}
}
