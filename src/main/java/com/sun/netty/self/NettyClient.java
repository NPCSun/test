package com.sun.netty.self;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
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

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by sun on 2017/9/27 上午9:54.
 */
@Component("nettyClient")
public class NettyClient {
	public void connect(String host, int port, final ApplicationContext context) throws InterruptedException {
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
							ClientHandler clientHandler = (ClientHandler)context.getBean("clientHandler");
							p.addLast(clientHandler);
						}
					});

			ChannelFuture future = b.connect(host, port).sync();

			future.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);

			future.channel().closeFuture();
		} finally {
			//group.shutdownGracefully();
		}
	}

	@ChannelHandler.Sharable
	@Service("clientHandler")
	public class ClientHandler extends SimpleChannelInboundHandler<TransferMessage> {

		private int count = 0;

		private long begin = System.currentTimeMillis();

		private Channel channel;

		Message message = new Message();

		public Channel getChannel() {
			return channel;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			channel = ctx.channel();
			message.setId(1000);
			message.setValue("i am form client");
			TransferMessage msg = new TransferMessage((byte)1, JSON.toJSONString(message));
			ctx.writeAndFlush(msg);
			ctx.executor().schedule(new HeartBeatTask(ctx), 5, TimeUnit.SECONDS);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, TransferMessage msg) throws Exception {
			/*ctx.writeAndFlush(transferMessage);
			count++;
			if(count%50000 == 0){
				long end = System.currentTimeMillis();
				System.out.println("client read 50000 transferMessage. 耗时(毫秒)：" + (end-begin));
				begin = end;
			}*/
			//***********************************************************
			DefaultFuture.received(msg);
			//***********************************************************
			/*Channel channel = ctx.channel();
			while(true){
				Thread.sleep(10000);

				if(channel.isWritable()){
					ctx.writeAndFlush(transferMessage);
				}

			}*/

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
		}
	}

	public static void main(String[] args) throws Exception {

		//new NettyClient().connect("127.0.0.1", 8081);
	}
}
