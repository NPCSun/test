package com.sun.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

	private static String data = "";
	/*
	 * static{ try { FileInputStream fis = new
	 * FileInputStream("d:\\Readme.txt"); byte[] result = new byte[1024];
	 * while(fis.read(result)!=-1){ data = data + new String(result); } } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } }
	 */

	/**
	 * 此方法会在连接到服务器后被调用
	 * 
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 * @param ctx
	 * @throws InterruptedException
	 */
	public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
		String data = "Meishizhaoshi message: sign up!aaabbbbbcccccddddd]]\r\n";
		while (true) {
			ctx.write(Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
			ctx.flush(); // System.out.println(count++);
			Thread.sleep(1);
			int size = 4 + "payload".getBytes().length;

	        ByteBuf buffer = Unpooled.buffer(size);
	        buffer.writeInt("payload".getBytes().length);
	        buffer.writeBytes("payload".getBytes());
	        ctx.writeAndFlush(Unpooled.copiedBuffer(buffer));
		}
		
		
	}

	/**
	 * 捕捉到异常
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg.getClass().getSimpleName().equals(String.class.getSimpleName())) {
			// 加入StringDecoder解码器时，直接输出
			System.out.println("client received data :" + msg);
		} else {
			// 未加解码器时，解码处理
			ByteBuf newbuf = ((ByteBuf) msg);
			byte[] dst = new byte[newbuf.readableBytes()];
			newbuf.readBytes(dst);
			String str = new String(dst);
			System.out.println("server received data :" + str);
			System.out.println(msg.getClass().getName());
		}

	}

}