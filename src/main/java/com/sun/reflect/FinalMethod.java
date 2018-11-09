package com.sun.reflect;

import java.lang.reflect.Method;

public class FinalMethod {
    public final void test(){

    }

    public static void main(String[] args) {
        Method[] methods = FinalMethod.class.getDeclaredMethods();
        for(Method method : methods){
            System.out.println(method.getModifiers());
            System.out.println(method.getName());
            System.out.println("------------------------");
        }
    }
}
