package com.sun.system;

public class SystemTest {
    public static void main(String[] args) {
        while(true){
            long startTime = System.nanoTime();
            int i = 0;
            i+=i;
            long endTime = System.nanoTime();
            System.out.println("耗时：\t" + (endTime-startTime));
        }
    }
}
