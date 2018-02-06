package com.sun.netty.udp.log;

import java.net.InetSocketAddress;

import lombok.Data;

/**
 * Created by sun on 2018/2/6 下午5:58.
 */
@Data
public final class LogEvent {
	public static final byte SEPARATOR = (byte) ':';

	private final InetSocketAddress source;

	private final String logfile;

	private final String msg;

	private final long received;

	public LogEvent(String logfile, String msg) {
		this(null, -1, logfile, msg);
	}

	public LogEvent(InetSocketAddress source, long received, String logfile, String msg) {
		this.source = source;
		this.logfile = logfile;
		this.msg = msg;
		this.received = received;
	}

}