package com.sun.netty.self;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by sun on 2017/9/28 下午5:59.
 */
public class HeartBeatTask implements Runnable{

	TransferMessage transferMessage = new TransferMessage();

	private final ChannelHandlerContext ctx;

	HeartBeatTask(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void run() {
		if (!ctx.channel().isOpen()) {
			return;
		}
		Message message = new Message();
		message.setId(1000);
		message.setValue("i am heartbeat message");
		transferMessage.setBody(JSON.toJSONString(message));
		ctx.writeAndFlush(transferMessage);

		ctx.executor().schedule(this, 5, TimeUnit.SECONDS);
	}
}
