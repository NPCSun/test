package com.sun.netty.self;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * Created by sun on 2017/9/26 上午11:22.
 */
public class MessageEncoder extends MessageToByteEncoder<TransferMessage> {
	private final Charset charset = Charset.forName("utf-8");

	@Override
	protected void encode(ChannelHandlerContext ctx, TransferMessage msg, ByteBuf out) throws Exception {
        byte[] data = msg.getBody().getBytes(charset);
        //
        out.writeByte(msg.getType());
        out.writeInt(data.length);
        out.writeLong(msg.getClientId());
        out.writeBytes(data);
        //ctx.channel().writeAndFlush(msg); //无需写出
	}
}
