package com.sun.opentracing;

import com.sun.reflect.AccessControlDemo;

/**
 * Created by sun on 2017/11/14 下午8:27.
 */
public class MyTracerFactory {
	public static void main(String[] args) {
		AccessControlDemo firstInstance = new AccessControlDemo("first instance");
		AccessControlDemo secondInstance = new AccessControlDemo("second instance");

		firstInstance.demoAccessOtherClass(secondInstance);
	}
}
