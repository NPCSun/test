package com.sun.guava;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Created by sun on 2017/10/17 下午2:16.
 */
public class MoreExecutorsTest {

	static void ListeningExecutorServiceTest(){
		ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
		final ListenableFuture listenableFuture = executorService.submit(new Runnable() {
			@Override
			public void run() {
				System.out.println("ListenableFuture callback execute..");
			}
		});
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		ListeningExecutorServiceTest();
	}
}
