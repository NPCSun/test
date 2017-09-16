package com.sun.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

//@Sharable
public class LoggerHandler extends ChannelDuplexHandler {
	
	@Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		ServerSocketChannel channel = (ServerSocketChannel) ctx.channel();
        System.out.println("channelRegistered:" + channel.getClass().getSimpleName());
    }
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
		if(msg instanceof NioSocketChannel){
			NioSocketChannel channel = (NioSocketChannel) msg;
			System.out.println(channel.remoteAddress());
		}
		ctx.fireChannelRead(msg);
	}

	public void channelReadComplete(ChannelHandlerContext ctx) {
		//异步do其他事情
		System.out.println("channelReadComplete");
		
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();// 捕捉异常信息
		ctx.close();// 出现异常时关闭channel
	}
}
