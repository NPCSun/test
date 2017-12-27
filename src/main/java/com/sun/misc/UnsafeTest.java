package com.sun.misc;

import static sun.misc.Unsafe.getUnsafe;

import java.io.Serializable;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.sun.agent.ObjectShallowSizeAgent;
import lombok.Data;
import sun.misc.Unsafe;

/**
 * Created by sun on 2017/10/17 上午10:16.
 a*/
public class UnsafeTest {

	private static Unsafe unsafe;

	static{
		Field unsafeField = null;
		try {
			unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			unsafe = (Unsafe) unsafeField.get(null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void allocateInstance(Class<?> clazz) throws InstantiationException {
		APO a = (APO) unsafe.allocateInstance(APO.class);
		a.setAge(10);
		System.out.println(a.getAge());
		System.out.println(a);
		Integer s1= new Integer(10);
		Integer s2= new Integer(10);
		System.out.println(s1==s2);
		int address1 = System.identityHashCode(s1);
		int address2 = System.identityHashCode(s2);
		System.out.println(Integer.toHexString(address1));
		System.out.println(Integer.toHexString(address2));
	}

	public static void shallowCopy() throws InstantiationException {

	}

	static long toAddress1(Object obj) {
		Object[] array =  new Object[] {obj};
		long baseOffset = unsafe.arrayBaseOffset(array.getClass());
		return normalize(unsafe.getInt(array, baseOffset));
	}


	public static long toAddress(Object o)
			throws Exception
	{
		Object[] array = new Object[] {o};

		long baseOffset = unsafe.arrayBaseOffset(Object[].class);
		int addressSize = unsafe.addressSize();
		long objectAddress = 0;
		switch (addressSize)
		{
			case 4:
				objectAddress = unsafe.getInt(array, baseOffset);
				break;
			case 8:
				objectAddress = unsafe.getLong(array, baseOffset);
				break;
			default:
				throw new Error("unsupported address size: " + addressSize);
		}

		return normalize(objectAddress);
	}

	private static long normalize(long value) {
		if(value >= 0) return value;
		return (~0L >>> 32) & value;
	}


	static APO fromAddress(long address) {
		APO[] array = new APO[] {null};
		long baseOffset = unsafe.arrayBaseOffset(APO[].class);
		unsafe.putLong(array, baseOffset, address);
		return array[0];
	}

	/**
	 * 没测试通过
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	static Object shallowCopy(Object obj) throws Exception {
		long size = ObjectShallowSizeAgent.sizeOf(obj);
		long start = toAddress(obj);
		long address = unsafe.allocateMemory(size);
		unsafe.copyMemory(start, address, size);
		return fromAddress(address);
	}

	public static void test() throws Exception {
		String mine = "Hi there";
		long address1 = toAddress(mine);
		System.out.println("mine Addess: " + address1);

		APO a = (APO) unsafe.allocateInstance(APO.class);
		int stringRefSize = unsafe.arrayIndexScale(APO[].class);
		System.out.println("APO Oop指针大小：\t" + stringRefSize);

		a.setName("sun");
		a.setAge(10);
		long size = ObjectShallowSizeAgent.sizeOf(a);
		System.out.println("APO对象size：\t" + size);

		Field[] fields = APO.class.getDeclaredFields();
		APO b = (APO) unsafe.allocateInstance(APO.class);
		address1 = toAddress(a);
		long address2= toAddress(b);
		System.out.println("Addess: " + address1);
		//8 + 4
		unsafe.putInt(b, 12l, a.getAge());
		Field field = APO.class.getDeclaredField("age");
		field.setAccessible(true);
		field.set(b, 100);
		System.out.println("b.getAge():" + field.get(b));


		for(Field f: fields){
			System.out.println(f.getName() + " offset: " +unsafe.objectFieldOffset(f));
			if("name".equals(f.getName())){
				unsafe.putOrderedObject(b, unsafe.objectFieldOffset(f), a.getName());
				System.out.println("APO对象b的name：\t" + b.getName());
			}
		}
		//8+2+4+2=16
		char[] s1 = new char[]{'a', 'b'};
		char[] s2 = new char[]{'1', '2'};

		size = ObjectShallowSizeAgent.sizeOf(s1);
		System.out.println("s1 size：\t" + size);
		long address = unsafe.allocateMemory(size);
		int stringBaseOffset = unsafe.arrayBaseOffset(char[].class);
		System.out.println("char stringBaseOffset：\t" + stringBaseOffset);
		int charRefSize = unsafe.arrayIndexScale(char[].class);
		System.out.println("char Oop指针大小：\t" + charRefSize);
		//unsafe.copyMemory目前测试 只能用于数组的拷贝？
		unsafe.copyMemory(s1, 16, s2,  16, 4);
		System.out.println(s2[1]);
	}

	static void clearPassword() throws Exception {
		String password = new String("sun@123");
		System.out.println(password); // l00k@myHor$e
		//unsafe.copyMemory(fake, 0L, null, toAddress1(password), ObjectShallowSizeAgent.sizeOf(password));
		Field stringValue = String.class.getDeclaredField("value");
		stringValue.setAccessible(true);
		char[] mem = (char[]) stringValue.get(password);
		for (int i=0; i < mem.length; i++) {
			mem[i] = '?';
		}
		System.out.println(password); // ????????????

	}

	public static void main(String[] args) throws Exception {

		//clearPassword();
		Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		System.out.println(ObjectShallowSizeAgent.sizeOf(map));
	}
}
