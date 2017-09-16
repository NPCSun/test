package com.sun.netty.handler;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	
	ConcurrentMap<String, Channel> channelMap;
	
	public EchoServerHandler(ConcurrentMap<String, Channel> channelMap){
		this.channelMap = channelMap;
	}
	
	private void recordClients(final ChannelHandlerContext ctx){
		Channel channel = ctx.channel();
		NioEventLoop eventLoop = (NioEventLoop) channel.eventLoop();
		//workers
		EventLoopGroup eventGroup = eventLoop.parent();
		System.out.println("eventGroup:" + eventGroup);
    	InetSocketAddress isa = (InetSocketAddress) channel.remoteAddress();
    	String ip = isa.getAddress().getHostAddress();
    	int port = isa.getPort();
    	channelMap.putIfAbsent(ip + ":" + port, channel);
	}
	
	@Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
		String data = "server: msg response]]\r\n";
	    final ChannelFuture f =  ctx.writeAndFlush(Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
            	recordClients(ctx);
            	if(future.isSuccess()){
            		assert f == future;
            		System.out.println("the i/o is finished!");
            		//ctx.close();
            	}
            }
        });
    }
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
		try{
			if(msg.getClass().getSimpleName().equals(String.class.getSimpleName())){
				//加入StringDecoder解码器时，直接输出
				System.out.println("server received data :" + msg);
				Thread.sleep(1000);
			}else{
				//未加解码器时，解码处理
				ByteBuf newbuf = ((ByteBuf) msg);
				byte[] dst = new byte[newbuf.readableBytes()];
				newbuf.readBytes(dst);
				String str = new String(dst);
				System.out.println("server received data :" + str);
				System.out.println(msg.getClass().getName());
			}
		} finally{
			ReferenceCountUtil.release(msg);
		}
	}

	public void channelReadComplete(ChannelHandlerContext ctx) {
		System.out.println("echoServerHandler channelReadComplete");
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();// 捕捉异常信息
		ctx.close();// 出现异常时关闭channel
	}
}
