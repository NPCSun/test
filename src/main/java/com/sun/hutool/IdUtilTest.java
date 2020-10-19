package com.sun.hutool;

import cn.hutool.core.util.IdUtil;

import java.util.UUID;

public class IdUtilTest {

    public static void javaUUID() {
        System.out.println("-------------------java UUID-------------------------------");
        int count = 0;
        long begin = System.currentTimeMillis();
        while (count <= 50) {
            String uuid = UUID.randomUUID().toString();
            System.out.println(uuid);
            System.out.println(uuid.replace('-', 'a').length());
            count++;
        }
        System.out.println("耗时（毫秒）：" + (System.currentTimeMillis() - begin));
    }

    public static void objectId() {
        System.out.println("-------------------objectId-------------------------------");
        int count = 0;
        long begin = System.currentTimeMillis();
        while (count <= 50) {
            System.out.println(IdUtil.objectId());
            count++;
        }
        System.out.println("耗时（毫秒）：" + (System.currentTimeMillis() - begin));
    }

    public static void simpleUUID() {
        System.out.println("-------------------simpleUUID-------------------------------");
        int count = 0;
        long begin = System.currentTimeMillis();
        while (count <= 50) {
            System.out.println(IdUtil.simpleUUID());
            count++;
        }
        System.out.println("耗时（毫秒）：" + (System.currentTimeMillis() - begin));
    }

    public static void main(String[] args) {
        javaUUID();
        //objectId();
        //simpleUUID();
    }
}
