package com.sun.netty.udp.log;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.Random;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * Created by sun on 2018/2/7 上午10:09.
 */
public class LogEventBroadcaster {
	private final EventLoopGroup group;

	private final Bootstrap bootstrap;

	private final File file;

	private long pointer;

	private Jedis jedis = new Jedis("10.0.0.213", 6379);

	public LogEventBroadcaster(InetSocketAddress address, File file) {
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new LogEventEncoder(address));
		this.file = file;
		//TODO 上次最后读到的位置
		jedis.auth("123456");
		String positionStr = jedis.get("logeventbroadcaster-last-postion");
		if(StringUtils.isBlank(positionStr)){
			pointer = 0;
		}else{
			pointer = Long.valueOf(positionStr);
		}
	}

	public void run() throws Exception {
		Channel ch = bootstrap.bind(0).sync().channel();
		String line;
		Random random = new Random();
		long sleepTime = 0;
		for (; ; ) {
			long len = file.length();
			if (len < pointer) {
				// file was reset
				pointer = len;
			} else if (len > pointer) {
				// Content was added
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				raf.seek(pointer);
				while ((line = raf.readLine()) != null) {
					ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
					System.out.println("有新的日志已经被广播出去...");
				}
				pointer = raf.getFilePointer();
				jedis.set("logeventbroadcaster-last-postion", String.valueOf(pointer));
				raf.close();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.interrupted();
					break;
				}
			}else{
				try {
					sleepTime = random.nextInt(1000);
					System.out.println(sleepTime);
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					//Thread.interrupted();
					e.printStackTrace();
				}
			}
		}
	}

	public void stop() {
		group.shutdownGracefully();
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException();
		}
		LogEventBroadcaster broadcaster =
				new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", Integer.parseInt(args[0])), new File(args[1]));
		try {
			broadcaster.run();
		} finally {
			broadcaster.stop();
		}
	}
}
