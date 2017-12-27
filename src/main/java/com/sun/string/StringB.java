package com.sun.string;

import java.lang.reflect.Field;

/**
 * Created by sun on 2017/12/17 上午10:54.
 */
public class StringB {
	public String  a = "123";


	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
		StringA a = new StringA();
		StringB b = new StringB();
		System.out.println("a.a == b.a\t" + (a.a == b.a));

		String s1 = "a" + new String("b");
		String s2= new String("ab");
		Field field = String.class.getDeclaredField("value");
		field.setAccessible(true);
		char[] v1 = (char[]) field.get(s1);
		field = String.class.getDeclaredField("value");
		field.setAccessible(true);
		char[] v2 = (char[]) field.get(s2);
		System.out.println("v1 == v2\t" + (v1==v2));

	}
}
