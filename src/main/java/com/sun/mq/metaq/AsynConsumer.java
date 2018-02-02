package com.sun.mq.metaq;

import java.util.concurrent.Executor;
import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.MetaClientConfig;
import com.taobao.metamorphosis.client.MetaMessageSessionFactory;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import com.taobao.metamorphosis.client.consumer.MessageListener;
import com.taobao.metamorphosis.utils.ZkUtils.ZKConfig;
 
public class AsynConsumer {
	
    public static void main(String[] args) throws Exception {
        final MetaClientConfig metaClientConfig = new MetaClientConfig();
        final ZKConfig zkConfig = new ZKConfig();
        zkConfig.zkConnect = "127.0.0.1:2181";
        zkConfig.zkRoot = "/meta";
        metaClientConfig.setZkConfig(zkConfig);
        //metaClientConfig.setServerUrl("meta://192.168.199.198:8123");
        MessageSessionFactory sessionFactory = new MetaMessageSessionFactory(metaClientConfig);
        final String topic = "sunmq";
        // consumer group
        final String group = "sunmq-group1";
        MessageConsumer consumer = sessionFactory.createConsumer(new ConsumerConfig(group));
        consumer.subscribe(topic, 1024, new MessageListener() {
             
            public void recieveMessages(Message message) {
                System.out.println("Receive message " + new String(message.getData()));
            }
            public Executor getExecutor() {
                // Thread pool to process messages,maybe null.
                return null;
            }
        });
        // complete subscribe
        consumer.completeSubscribe();
    }
}