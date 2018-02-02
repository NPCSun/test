package com.sun.mq.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;


/**
 * Created by sun on 2017/12/26 下午3:56.
 */
public class KafkaProducerTest {
	public static void main(String[] args) {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.0.217:9092");
		props.put(ProducerConfig.RETRIES_CONFIG, 1);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 2);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 40960);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		ProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(props);
		KafkaTemplate<String, String> template = new KafkaTemplate<String, String>(factory, true);
		template.send("test", "hello kafka from docker!");
		template.flush();
	}
}
