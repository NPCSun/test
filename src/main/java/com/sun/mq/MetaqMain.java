package com.sun.mq;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.metamorphosis.client.extension.spring.MessageBuilder;
import com.taobao.metamorphosis.client.extension.spring.MetaqTemplate;
import com.taobao.metamorphosis.client.producer.SendResult;

public class MetaqMain {

	public static void main(String[] args) {
		CountDownLatch cdl = new CountDownLatch(1);
		String config = "classpath*:config/deploy/spring-*.xml";
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(config);
	    context.start();
	    MetaqTemplate metaqTemplate = context.getBean("metaqTemplate", MetaqTemplate.class);
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
	    /*try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}*/
	    
	    
	}
}
