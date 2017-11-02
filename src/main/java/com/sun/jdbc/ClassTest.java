package com.sun.jdbc;

/**
 * Created by sun on 2017/10/27 上午10:51.
 */
public class ClassTest {
	public static void main(String[] args) {
		Class<?> clazz = String.class;

		String string = new String("123");

		System.out.println(clazz.isInstance(string));
	}
}
