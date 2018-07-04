package com.sun.netty.self;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by sun on 2017/9/27 上午9:54.
 */
@Component("nettyClient")
public class NettyClient {
    public void connect(String serverHost, int serverport, final ApplicationContext context) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    //.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024))//1KB=1024*8byte=8192byte
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ClientMessageDecoder(1 << 20, 1, 4, 8, 0));
                            p.addLast(new MessageEncoder());
                            ClientHandler clientHandler = null;
                            if (context != null) {
                                clientHandler = (ClientHandler) context.getBean("clientHandler");
                            } else {
                                clientHandler = new ClientHandler();
                            }
                            p.addLast(clientHandler);
                        }
                    });

            //SocketAddress remoteAddress = InetSocketAddress.createUnresolved(serverHost, serverport);
            //SocketAddress localAddress = InetSocketAddress.createUnresolved("127.0.0.1", 9000);
            //ChannelFuture future = b.connect(remoteAddress, null).sync();

            ChannelFuture future = b.connect(serverHost, serverport).sync();
            future.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);
            future.channel().closeFuture().sync();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            System.err.println("");
            System.err.println("服务端不可用，连接关闭，程序退出!!!!!!!!!!!!!");
        }
    }

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
            message.setValue("i am from client.");
            TransferMessage msg = new TransferMessage((byte) 1, JSON.toJSONString(message), 9999);
            ctx.writeAndFlush(msg);
            //客户端否则发送心跳包，服务端心跳包
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
            Channel channel = ctx.channel();

            if (channel.isWritable()) {
                message.setId(8888);
                message.setValue("i am from client.");
                ctx.writeAndFlush(message);
            }


        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            //cause.printStackTrace();
            //ctx.close();
        }
    }

    public static void main(String[] args) throws Exception {

        new NettyClient().connect("127.0.0.1", 8888, null);

    }
}
