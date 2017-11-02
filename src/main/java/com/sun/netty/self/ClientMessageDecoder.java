package com.sun.netty.self;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by sun on 2017/9/26 上午11:17.
 */
public class ClientMessageDecoder extends LengthFieldBasedFrameDecoder {
	/** 头长*/
	private static final int HEADER_SIZE = 5;

	public ClientMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		if (in == null) {
			return null;
		}

		if (in.readableBytes() <= HEADER_SIZE) {
			return null;
		}

		in.markReaderIndex();

		byte type = in.readByte();
		int dataLength = in.readInt();

		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return null;
		}

		byte[] data = new byte[dataLength];
		in.readBytes(data);

		String body = new String(data, "UTF-8");
		TransferMessage msg = new TransferMessage(type, dataLength, body);

		return msg;
	}
}
