package com.sun.netty.self;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by sun on 2017/9/28 下午5:59.
 */
public class HeartBeatTask implements Runnable{
	String body = "i am heartbeat. ";
	Message msg = new Message((byte)0, body);

	private final ChannelHandlerContext ctx;

	HeartBeatTask(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void run() {
		if (!ctx.channel().isOpen()) {
			return;
		}

		ctx.writeAndFlush(msg);

		ctx.executor().schedule(this, 5, TimeUnit.SECONDS);
	}
}
