package com.sun.netty.udp.log;

import java.net.InetSocketAddress;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by sun on 2018/2/6 下午5:59.
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
	private final InetSocketAddress remoteAddress;

	public LogEventEncoder(InetSocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext,
			LogEvent logEvent, List<Object> out) throws Exception {
		byte[] file = logEvent.getLogfileName().getBytes(CharsetUtil.UTF_8);
		byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
		ByteBuf buf = channelHandlerContext.alloc()
				.buffer(file.length + msg.length + 1);
		buf.writeBytes(file);
		buf.writeByte(LogEvent.SEPARATOR);
		buf.writeBytes(msg);
		out.add(new DatagramPacket(buf, remoteAddress));
	}
}

