package com.sun.guava;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Created by sun on 2017/8/29 上午10:18.
 */
public class EventBusTest {
	public static void main(String[] args) {
		final EventBus eventBus = new EventBus();
		eventBus.register(new Object() {

			@Subscribe
			public void lister(final String message) {
				System.out.printf("收到消息：" + message);
			}

		});

		eventBus.post("该干活了！");
	}
}
