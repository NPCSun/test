package com.sun.misc;

import static sun.misc.Unsafe.getUnsafe;

import java.io.Serializable;
import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Created by sun on 2017/10/17 上午10:16.
 */
public class UnsafeTest {
	class A implements Serializable {
		private  int num;
		public A(int num) {
			System.out.println("Hello Mum");
			this.num = num;
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num){
			this.num = num;
		}
	}

	public static void main(String[] args) throws InstantiationException, NoSuchFieldException, IllegalAccessException {
		Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
		unsafeField.setAccessible(true);
		Unsafe unsafe = (Unsafe) unsafeField.get(null);
		A a = (A) unsafe.allocateInstance(A.class);
		a.setNum(10);
		System.out.println(a.getNum());
		System.out.println(a);
		Integer s1=10;
		Integer s2=10;
		System.out.println(s1==s2);
		int address1 = System.identityHashCode(s1);
		int address2 = System.identityHashCode(s1);
		System.out.println(Integer.toHexString(address1));
		System.out.println(Integer.toHexString(address2));


	}
}
