package com.sun.netty.self;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by sun on 2017/9/26 上午11:17.
 */
public class ClientMessageDecoder extends LengthFieldBasedFrameDecoder {
    /**
     * 头长
     */
    private static final int HEADER_SIZE = 4;

    public ClientMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);

        if (frame == null) {
            return null;
        }

        //包类型判断
        byte type = frame.readByte();
        //
        int dataLength = frame.readInt();
        //
        long serverId = frame.readLong();

        System.out.println("serverId:\t" + serverId);

        byte[] data = new byte[dataLength];
        frame.readBytes(data);

        String body = new String(data, "UTF-8");

        return new TransferMessage(type, dataLength, body);

    }
}
