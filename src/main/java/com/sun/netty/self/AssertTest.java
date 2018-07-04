package com.sun.netty.self;

public class AssertTest {

    public static boolean Check(){
        return true;
    }

    public static void print(){
        assert Check();
        System.out.println("执行了！");
    }
    public static void main(String[] args) {
        print();
    }
}
