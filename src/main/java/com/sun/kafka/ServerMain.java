package com.sun.kafka;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by sun on 2017/12/26 下午4:41.
 */
public class ServerMain {

	public static void main(String[] args) throws InterruptedException, SQLException {
		CountDownLatch cdl = new CountDownLatch(1);
		String config = "classpath*:config/deploy/kafka-consumer.xml";
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(config);
		context.start();
		System.out.println("spring container started, waiting for kafka msg.");
		try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally{
			cdl.countDown();
		}
	}
}
