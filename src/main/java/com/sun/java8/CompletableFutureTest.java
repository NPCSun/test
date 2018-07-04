package com.sun.java8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

/**
 * Created by sun on 2018/2/8 上午9:35.
 */
public class CompletableFutureTest {

    @Test
    public static void thenApply() {                        //{return "hello";}
        String result = CompletableFuture.supplyAsync(() -> "hello").thenApply(s -> s + " world").join();
        System.out.println(result);
    }

    public static void test1() {
        CompletableFuture<String> completableFuture = new CompletableFuture();
        new Thread(() -> { //模拟执行耗时任务
            System.out.println("task doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //告诉completableFuture任务已经完成
            completableFuture.complete("12345");
        }).start();
        //获取任务结果，如果没有完成会一直阻塞等待
        String result = null;
        try {
            result = completableFuture.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("计算结果:" + result);
    }

    public static void test2() {
        CompletableFuture<String> completableFuture = new CompletableFuture();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //模拟执行耗时任务
                    System.out.println("task doing...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    throw new RuntimeException("抛异常了");
                } catch (Exception e) {
                    //告诉completableFuture任务发生异常了
                    completableFuture.completeExceptionally(e);
                }
            }
        }).start();
        //获取任务结果，如果没有完成会一直阻塞等待
        String result = null;
        try {
            result = completableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("计算结果:" + result);

    }

    public static void test3() {
        //supplyAsync内部使用ForkJoinPool线程池执行任务
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            //模拟执行耗时任务
            System.out.println("task doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return "result";
        });
        try {
            System.out.println("计算结果:" + completableFuture.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void test4() throws Exception {

        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            //模拟执行耗时任务
            System.out.println("task1 doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return "result1";
        });

        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            //模拟执行耗时任务
            System.out.println("task2 doing...");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return "result2";
        });

        CompletableFuture<Object> anyResult = CompletableFuture.anyOf(completableFuture1, completableFuture2);

        System.out.println("anyOf -> 第一个完成的任务结果:" + anyResult.get());

        CompletableFuture<Void> allResult = CompletableFuture.allOf(completableFuture1, completableFuture2);

        //阻塞等待所有任务执行完成
        allResult.join();
        System.out.println("allOf -> 所有任务执行完成");

    }

    /**
     * 通常，我们会有多个需要独立运行但又有所依赖的的任务。比如先等用于的订单处理完毕然后才发送邮件通知客户。
     * thenCompose 方法允许你对两个异步操作进行<<--流水线-->>，第一个操作完成时，将其结果作为参数传递给第二个操作。
     * 你可以创建两个CompletableFutures 对象，对第一个 CompletableFuture 对象调用thenCompose ，并向其传递一个函数。
     * 当第一个CompletableFuture 执行完毕后，它的结果将作为该函数的参数，这个函数的返回值是以第一个 CompletableFuture 的返回做输入计算出的第二个 CompletableFuture 对象。
     *
     * @throws Exception
     */
    public static void test5() throws Exception {

        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            //模拟执行耗时任务
            System.out.println("task1 doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return "result1";
        });

        //等第一个任务完成后，将任务结果传给参数result，执行后面的任务并返回一个代表任务的completableFuture
        CompletableFuture<String> completableFuture2 = completableFuture1.thenCompose(result -> CompletableFuture.supplyAsync(() -> {
            //模拟执行耗时任务
            System.out.println("task2 doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return "result2";
        }));

        System.out.println(completableFuture2.get());

    }

    /**
     * 响应 CompletableFuture 的 completion 事件
     * 我们可以在每个CompletableFuture 上注册一个操作，该操作会在 CompletableFuture 完成执行后调用它。
     * CompletableFuture 通过 thenAccept 方法提供了这一功能，它接收CompletableFuture 执行完毕后的返回值做参数。
     *
     * @throws Exception
     */
    public static void test7() throws Exception {

        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            //模拟执行耗时任务
            System.out.println("task1 doing...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //返回结果
            return 100;
        });

        //注册完成事件
        completableFuture1.thenAccept(result -> System.out.println("task1 done,result:" + result));

        CompletableFuture<Integer> completableFuture2 =
                //第二个任务
                CompletableFuture.supplyAsync(() -> {
                    //模拟执行耗时任务
                    System.out.println("task2 doing...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //返回结果
                    return 2000;
                });

        //注册完成事件
        completableFuture2.thenAccept(result -> System.out.println("task2 done,result:" + result));

        //将第一个任务与第二个任务组合一起执行，都执行完成后，将两个任务的结果合并
        CompletableFuture<Integer> completableFuture3 = completableFuture1.thenCombine(completableFuture2,
                //合并函数
                (result1, result2) -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return result1 + result2;
                });

        System.out.println(completableFuture3.get());

    }

    public static void main(String[] args) throws Throwable {
        //thenApply();
        test1();
    }
}
