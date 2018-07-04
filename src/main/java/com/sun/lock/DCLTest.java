package com.sun.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//-XX:CompileCommand=compileonly,*DCLTest.getInstance
public class DCLTest {
    public static volatile DCLTest instance;

    public static DCLTest getSun() {
        if (instance == null)              //1
        {                                  //2
            if (instance == null)          //4
                instance = new DCLTest();  //5
        }
        return instance;
    }

    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DCLTest.getSun();
            }
        });
        t.start();
    }
}
