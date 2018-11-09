package com.sun.netty.self;

import java.util.concurrent.ThreadFactory;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by sun on 2017/9/26 上午11:24.
 */
@Component("nettyServer")
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
                            //lengthAdjustment=8, the length of clientId(Long)
                            p.addLast(new ServerMessageDecoder(1 << 20, 1, 4, 8, 0));
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

    @Service("serverHandler")
    public class ServerHandler extends SimpleChannelInboundHandler<TransferMessage> {

        private long begin = System.currentTimeMillis();

        private int count = 0;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TransferMessage transferMessage) throws Exception {
            TransferMessage resp = new TransferMessage((byte) 1, 0, "Hello world from server");
            Message message = JSON.parseObject(transferMessage.getBody(), Message.class);
            if (message != null) {
                System.out.println(message.getValue());
                //

                if (1001 == message.getId()) {
                    System.out.println(message.getValue());
                    Message response = new Message();
                    response.setId(1001);
                    response.setValue("RPC response]]]]]]]]]]]]]");
                    transferMessage.setBody(JSON.toJSONString(response));
                    ctx.writeAndFlush(transferMessage);
                }
                resp.setClientId(001);
                ctx.channel().writeAndFlush(resp);
                if (count != 0 && count % 100000 == 0) {
                    long end = System.currentTimeMillis();
                    System.out.println("收到 100000条 transferMessage. 耗时(毫秒)：" + (end - begin));
                    begin = end;
                }
                count++;
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

        new NettyServer().bind(8888);
    }
}
