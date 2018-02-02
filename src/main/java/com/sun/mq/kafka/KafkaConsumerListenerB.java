package com.sun.mq.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Created by sun on 2017/12/26 下午4:34.
 */
@Component
public class KafkaConsumerListenerB {

	@KafkaListener(topics = { "test" }, groupId = "B")
	public void taskCmd(ConsumerRecord<?, ?> record) {
		Object message = record.value();
		System.out.println("B收到管理平台命令:" + message);
	}
}
