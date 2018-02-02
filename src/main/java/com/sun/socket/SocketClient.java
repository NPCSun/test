package com.sun.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import redis.clients.jedis.Protocol;
import redis.clients.jedis.Protocol.Command;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.RedisInputStream;
import redis.clients.util.RedisOutputStream;
import redis.clients.util.SafeEncoder;

public class SocketClient {
	private int timeout = 3;
	private String host = "10.0.0.213";
	private int port = 6379;
	private Socket socket;
	private RedisInputStream inputStream;
	public static final byte DOLLAR_BYTE = '$';
	public static final byte ASTERISK_BYTE = '*';
	public static final byte PLUS_BYTE = '+';
	public static final byte MINUS_BYTE = '-';
	public static final byte COLON_BYTE = ':';

	public static final byte[] toByteArray(final int value) {
		return SafeEncoder.encode(String.valueOf(value));
	}

	public boolean isConnected() {
		return socket != null && socket.isBound() && !socket.isClosed() && socket.isConnected()
				&& !socket.isInputShutdown() && !socket.isOutputShutdown();
	}

	public void connect() {
		if (!isConnected()) {
			try {
				socket = new Socket();
				// ->@wjw_add
				socket.setReuseAddress(true);
				socket.setKeepAlive(true); // Will monitor the TCP connection is
											// valid
				socket.setTcpNoDelay(true); // Socket buffer Whetherclosed, to
											// ensure timely delivery of data
				socket.setSoLinger(true, 5); // Control calls close () method,
												// the underlying socket is
												// closed immediately
				// <-@wjw_add

				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);
				final RedisOutputStream os = new RedisOutputStream(socket.getOutputStream());
				final Command ttl = Protocol.Command.TTL;
				final Command get = Protocol.Command.GET;
				final byte[] key = SafeEncoder.encode(String.valueOf("redis"));
				try {
					os.write(ASTERISK_BYTE);
					os.writeIntCrLf(1 + 1);
					os.write(DOLLAR_BYTE);
					/*os.writeIntCrLf(ttl.raw.length);
					os.write(ttl.raw);*/
					os.writeIntCrLf(get.raw.length);
					os.write(get.raw);
					os.writeCrLf();  
					//
					os.write(DOLLAR_BYTE);
					os.writeIntCrLf(key.length);
					os.write(key);
					os.writeCrLf();
					os.flush();
				} catch (IOException e) {
					throw new JedisConnectionException(e);
				}

				inputStream = new RedisInputStream(socket.getInputStream());
				byte[] reply = new byte[1024];
				inputStream.read(reply);
				String replyStr = new String(reply);
				System.out.println(replyStr);
			} catch (Exception ex) {
				ex.printStackTrace();
				// swallow
				// throw new JedisConnectionException(ex);
			}
		}
	}

	public static void main(String[] args) {
		SocketClient sc = new SocketClient();
		sc.connect();

		/*CountDownLatch cdl = new CountDownLatch(1);
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);*/

	}
}
