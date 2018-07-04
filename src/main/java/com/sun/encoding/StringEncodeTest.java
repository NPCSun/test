package com.sun.encoding;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sun on 2018/2/28 下午3:00.
 */
public class StringEncodeTest {
	public static void main(String[] args) throws UnsupportedEncodingException {
		String str = "汉";//这个就是正常显示的字符串
		char[] chars = str.toCharArray();

		byte[] b1 = str.getBytes("unicode");
		List list1 = Arrays.asList(b1);
		System.out.println(Arrays.deepToString(list1.toArray()));

		String str2 = "ab";//这个就是正常显示的字符串
		char[] chars2 = str.toCharArray();

		byte[] b2 = str2.getBytes("unicode");
		List list2= Arrays.asList(b2);
		System.out.println(Arrays.deepToString(list2.toArray()));

		/*byte[] b2 = str.getBytes("utf-8");
		List list2 = Arrays.asList(b2);
		System.out.println(Arrays.deepToString(list2.toArray()));*/

		byte[] b3 = str.getBytes("gb2312");
		List list3 = Arrays.asList(b3);
		System.out.println(Arrays.deepToString(list3.toArray()));

		System.out.println(0xE6 & 0xF0);


	}
}
