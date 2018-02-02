package com.sun.mq.metaq;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.sun.jdbc.ShardingJdbcService;
import com.sun.jdbc.ShardingJdbcTest;
import com.sun.netty.self.DefaultFuture;
import com.sun.netty.self.Message;
import com.sun.netty.self.TransferMessage;
import com.sun.netty.self.NettyClient;
import io.netty.channel.Channel;

public class MetaqMain {


	public static void main(String[] args) throws InterruptedException, SQLException {
		CountDownLatch cdl = new CountDownLatch(1);
		String config = "classpath*:config/deploy/spring-total.xml";
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(config);
	    context.start();
		/*ExecutorService es = Executors.newFixedThreadPool(1);
		es.submit(new Runnable() {
			@Override
			public void run() {
				NettyClient client = (NettyClient) context.getBean("nettyClient");
				try {
					client.connect("127.0.0.1", 8082, context);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		Thread.sleep(3000);
		NettyClient.ClientHandler clientHandler = (NettyClient.ClientHandler)context.getBean("clientHandler");
		Channel channel = clientHandler.getChannel();
		Message message = new Message();
		message.setId(1001);
		message.setValue("RPC invoke");
		TransferMessage transferMessage = new TransferMessage((byte)1, JSON.toJSONString(message));
		//
		DefaultFuture future = new DefaultFuture(channel, message);

		channel.writeAndFlush(transferMessage);

		Message result = (Message)future.get(3000);

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>  " + result.getValue() + "  <<<<<<<<<<<<<<<<<<<<<<<<<");*/

		ShardingJdbcService shardingJdbcService = context.getBean("shardingJdbcService", ShardingJdbcService.class);
		//shardingJdbcService.testInsertTransaction();
		shardingJdbcService.testSelect();
		/*MetaqTemplate metaqTemplate = context.getBean("metaqTemplate", MetaqTemplate.class);
	    final String topic = "sunmq";
	    try {
			final SendResult sendResult = metaqTemplate
							.send(MessageBuilder.withTopic(topic).withBody(new Date()));
			if (!sendResult.isSuccess()) {
				System.err.println("Send message failed,error message:" + sendResult.getErrorMessage());
			} else {
				System.out.println("Send message successfully,sent to " + sendResult.getPartition());
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally{
			cdl.countDown();
		}
	    *//*try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}*/
	    
	    
	}
}
