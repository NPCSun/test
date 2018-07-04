package com.sun.netty.udp.log;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

/**
 * Created by sun on 2018/2/7 上午9:31.
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
	@Override
	protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
			ByteBuf data = datagramPacket.content();
			int idx = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
			String filename = data.slice(0, idx).toString(CharsetUtil.UTF_8);
			String logMsg = data.slice(idx + 1, data.readableBytes()).toString(CharsetUtil.UTF_8);
			LogEvent event = new LogEvent(datagramPacket.sender(), System.currentTimeMillis(), filename, logMsg);
			out.add(event);
		}
	}
