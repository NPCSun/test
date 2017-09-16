package com.sun.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * NIO瀹㈡埛绔�
 */
public class NIOClient implements Runnable{
	private final Selector selector;

	public static String data = "Ack from client.";

	public NIOClient(String ip, int port) throws IOException {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.selector = Selector.open();

		channel.connect(new InetSocketAddress(ip, port));
		channel.register(selector, SelectionKey.OP_CONNECT);
	}

	/**
	 * @throws IOException
	 */
	public void listen() {
		int seqNum = 0;
		try {
			int count = 0;
			while (true) {
				//count = selector.selectNow();//太耗CPU
				count = selector.select(5000);
				Iterator<?> ite = this.selector.selectedKeys().iterator();
				while (ite.hasNext()) {
					SelectionKey key = (SelectionKey) ite.next();
					ite.remove();
					if (key.isConnectable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						//
						if (channel.isConnectionPending()) {
							channel.finishConnect();
						}
						channel.configureBlocking(false);


						channel.write(ByteBuffer.wrap(data.getBytes("UTF-8")));
						channel.register(selector, SelectionKey.OP_READ);
							/*try {
								Thread.sleep(4);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/


					} else if (key.isReadable()) {
						read(key);
					}

				}

			}
		} catch (IOException e) {
			System.out.println(seqNum);
			e.printStackTrace();
		}
	}


	public void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(16);
		channel.read(buffer);
		while (buffer.hasRemaining()) {
			channel.read(buffer);
		}
		byte[] data = null;
		if (buffer.hasArray()) {
			data = buffer.array();
			String msg = new String(data, "utf-8").trim();
			System.out.println("client收到消息" + msg);
			ByteBuffer outBuffer = ByteBuffer.wrap("Ack from client.".getBytes("UTF-8"));
			/*try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			channel.write(outBuffer);// 
		}
	}

	public static void test() {
		try {
			Selector selector1 = Selector.open();
			Selector selector2 = Selector.open();

			SocketChannel channel1 = SocketChannel.open();
			SocketChannel channel2 = SocketChannel.open();
			channel1.configureBlocking(false);
			channel2.configureBlocking(false);

			channel1.connect(new InetSocketAddress("127.0.0.1", 8000));
			channel2.connect(new InetSocketAddress("127.0.0.1", 8000));

			channel1.register(selector1, SelectionKey.OP_CONNECT);
			channel2.register(selector2, SelectionKey.OP_CONNECT);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
//		test();
		int count = 2;
		ExecutorService es = Executors.newFixedThreadPool(count);
		NIOClient client;
		for(int i=0; i< count; i++){
			client = new NIOClient("127.0.0.1", 8000);
			es.execute(client);
		}
		es.awaitTermination(100, TimeUnit.MINUTES);
	}


	public void run() {
		listen();
	}
}
