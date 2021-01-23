package com.sun.elasticsearch;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * function:
 * 
 * @author sunking
 * @date 2021/1/13 5:23 下午
 **/
@Component
@Slf4j
public class EsRestUtil {

	@Value("${elasticsearch.ip:127.0.0.1}")
	private String				ES_SERVER_IP;

	@Value("${elasticsearch.port:9200}")
	private int					ES_SERVER_HTTP_PORT;

	private RestHighLevelClient	client;

	@PostConstruct
	public void start() throws Exception {
		RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
				// 集群节点
				new HttpHost(ES_SERVER_IP, ES_SERVER_HTTP_PORT, "http")));
		this.client = client;
		Runtime.getRuntime().addShutdownHook(new Thread("EsRestUtil") {
			@Override
			public void run() {
				log.info("开始 close elasticsearch client");
				shutdown();
				log.info("成功关闭 elasticsearch client");
			}
		});
	}

	public void shutdown() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public RestHighLevelClient getClient() {
		return client;
	}
}