package com.sun.netty.self;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by sun on 2017/9/26 上午11:17.
 */
public class ServerMessageDecoder extends LengthFieldBasedFrameDecoder {
    /**
     * 头长
     */
    private static final int HEADER_SIZE = 4;

    private static final long DEFAULT_HEARTBEAT_TIMEOUT = 15 * 1000;

    /**
     * 维护客户端心跳超时
     * key channelId,
     */
    public static final ConcurrentMap<Long, Long> channelsSet = new ConcurrentHashMap<Long, Long>();

    public ServerMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
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
        long clientId = frame.readLong();

        System.out.println("clientId:\t" + clientId);

        byte[] data = new byte[dataLength];
        frame.readBytes(data);

        String body = new String(data, "UTF-8");
        TransferMessage msg = new TransferMessage(type, dataLength, body);

        return msg;

    }
}
