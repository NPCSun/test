package com.sun.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * NIO服务端
 *
 */
public class NIOServer {

	private AtomicLong counter = new AtomicLong(0);

	private ExecutorService workersThread = Executors.newFixedThreadPool(5);

	private ReadWriteReactor[] readWriteReactors = new ReadWriteReactor[3];

	private final AtomicInteger childIndex = new AtomicInteger();

	/**
	 * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
	 *
	 * @param port
	 *            绑定的端口号
	 * @throws IOException
	 */
	public void initServerAndStart(int port) throws IOException {
		// 获得一个ServerSocket通道
		ServerSocketChannel serverSocketChannel;
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket1 = serverSocketChannel.socket();
		serverSocket1.bind(new InetSocketAddress("127.0.0.1", port)); //一个ip+port只能对应一个serversocket。
		Selector selector1 = Selector.open();
		Selector selector2 = Selector.open();
		new Thread(new AcceptReactor(serverSocketChannel, selector1, "a")).start();
		new Thread(new AcceptReactor(serverSocketChannel, selector2, "b")).start();
		for (int i = 0; i < readWriteReactors.length; i++) {
			readWriteReactors[i] = new ReadWriteReactor();
		}
	}

	/**
	 * 读写reactor
	 */
	class ReadWriteReactor implements Runnable {

		//TODO 后期考虑ioRatio
		//List<SocketChannel> channelList = new ArrayList<>(100);

		private volatile boolean started = false;

		private final Selector readAndWriteSelector = Selector.open();

		ReadWriteReactor() throws IOException {

		}

		public void register(SocketChannel socketchannel) throws ClosedChannelException {
			//channelList.add(socketchannel);
			socketchannel.register(readAndWriteSelector, SelectionKey.OP_READ);
			if (!started) {
				started = true;
				new Thread(this).start();
			}
		}

		public void run() {
			try {
				while (true) {
					// 当注册的事件到达时，方法返回；否则,该方法会一直阻塞
					this.readAndWriteSelector.select(5000);
					// 获得selector中选中的项的迭代器，选中的项为注册的事件
					Iterator<?> ite = this.readAndWriteSelector.selectedKeys().iterator();
					System.out.println(ite);
					//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +this.acceptorSelector.selectedKeys().size());
					while (ite.hasNext()) {
						SelectionKey key = (SelectionKey) ite.next();
						// 删除已选的key,以防重复处理
						ite.remove();
						// 获得了可读的事件
						if (key.isReadable()) {
							// 如果解码涉及的业务操作很简单，可以直接在nio线程完成;
							// 如果解码涉及的业务处理复杂，会引入新的io阻塞，则需要在专门的业务线程池下完成。
							// 服务器可读取消息:得到事件发生的Socket通道
							ReadWriteWorker task = new ReadWriteWorker((SocketChannel) key.channel());
							workersThread.submit(task);
							//Thread.sleep(2);
						}
					} // end of while
					/*for(SocketChannel socketchannel : channelList){
						socketchannel.register(readAndWriteSelector, SelectionKey.OP_READ);
					}*/
				} // end of while
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (readAndWriteSelector != null && readAndWriteSelector.isOpen()) {
						readAndWriteSelector.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class AcceptReactor implements Runnable {

		private final ServerSocketChannel serverChannel;

		private final Selector acceptorSelector;

		private final String acceptorName;

		private volatile boolean needNewReactor = false;

		protected AcceptReactor(ServerSocketChannel serverChannel, Selector acceptorSelector, String acceptorName) throws IOException {
			this.serverChannel = serverChannel;
			this.acceptorSelector = acceptorSelector;
			this.acceptorName = acceptorName;
			try {
				// 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
				// 当该事件到达时，acceptorSelector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
				this.serverChannel.register(acceptorSelector, SelectionKey.OP_ACCEPT);
			} catch (ClosedChannelException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
		 *
		 * @throws IOException
		 */
		public void run() {
			System.out.println("服务端启动成功！");
			// 轮询访问selector
			try {
				while (true) {
					// 当注册的事件到达时，方法返回；否则,该方法会一直阻塞
					acceptorSelector.select(5000);
					// 获得selector中选中的项的迭代器，选中的项为注册的事件
					Iterator<?> ite = this.acceptorSelector.selectedKeys().iterator();
					//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +this.acceptorSelector.selectedKeys().size());
					while (ite.hasNext()) {
						SelectionKey key = (SelectionKey) ite.next();
						// 删除已选的key,以防重复处理
						ite.remove();
						// 客户端请求连接事件
						if (key.isAcceptable()) {
							ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
							// 获得和客户端连接的通道
							SocketChannel socketchannel = serverSocketChannel.accept();
							if (socketchannel != null) {
								InetSocketAddress remoteAddress = (InetSocketAddress) socketchannel.getRemoteAddress();
								InetSocketAddress localAddress = (InetSocketAddress) socketchannel.getLocalAddress();
								System.out.println("acceptor:" + this.acceptorName
										+ "[[remote:" + remoteAddress.getHostString() + "[[" + remoteAddress.getPort() +
										"[[local port:" + localAddress.getPort());
								// 设置成非阻塞
								socketchannel.configureBlocking(false);
								// 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
								// 获得和客户端连接的通道
								try {
									ReadWriteReactor reactor = readWriteReactors[Math.abs(childIndex.getAndIncrement() % readWriteReactors.length)];
									reactor.register(socketchannel);
								} catch (ClosedChannelException e) {
									e.printStackTrace();
								}
							}
						}

					} // end of while
				} // end of while
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (acceptorSelector != null && acceptorSelector.isOpen()) {
						acceptorSelector.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//-XX:NewRatio=1
	private class ReadWriteWorker implements Runnable {
		private ByteBuffer buffer = ByteBuffer.allocate(16);

		private SocketChannel soChannel;

		public ReadWriteWorker(SocketChannel... soChannels) {
			if (soChannels.length > 0) {
				this.soChannel = soChannels[0];
			} else if (soChannels.length == 0) {
				throw new RuntimeException("need socketChannel!");
			}
		}

		/**
		 * 处理读取客户端发来的信息
		 *
		 */
		public void run() {
			try {
				//System.out.println(soChannel.getRemoteAddress());
				soChannel.read(buffer);
				while (buffer.hasRemaining()) {
					soChannel.read(buffer);
				}
				String msg = new String(buffer.array(), "utf-8");
				//System.out.println("server收到新消息：" + msg);
				soChannel.write(ByteBuffer.wrap("Ack from server.".getBytes("UTF-8")));
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				//
			}
		}
		//
	}


	/**
	 * 启动服务端测试
	 *
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		NIOServer server = new NIOServer();
		server.initServerAndStart(9000);

	}

}