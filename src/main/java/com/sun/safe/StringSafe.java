package com.sun.safe;

import static sun.misc.Unsafe.getUnsafe;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Created by sun on 2017/11/30 下午10:34.
 */
public class StringSafe {
	static Unsafe unsafe;
	static {
		Field f = null;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			unsafe = (Unsafe) f.get(null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}
	private static final long longArrayOffset = unsafe.arrayBaseOffset(long[].class);
	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

		String password = new String("sun");
		String fake = new String(password.replaceAll(".", "?"));
		System.out.println(password); // l00k@myHor$e
		System.out.println(fake); // ????????????

		char[] pass = new char[]{'a','b'};
		char[] tmp = new char[]{'?','?'};
		System.identityHashCode(password);
		long longArrayOffset = unsafe.arrayBaseOffset(char[].class);
		unsafe.copyMemory(tmp, longArrayOffset,
				pass, longArrayOffset, 4l);
		for(char s : pass){
			System.out.print(s);
		}
	}
}
