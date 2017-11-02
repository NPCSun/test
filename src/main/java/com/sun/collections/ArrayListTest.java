package com.sun.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sun on 2017/11/1 上午11:01.
 */
public class ArrayListTest {
	public static void main(String[] args) {
		String[] arr1 = new String[1];
		arr1[0] = "0";
		String[] arr2 = Arrays.copyOf(arr1, 3);
		arr2[1] = "1";
		System.out.println(arr2.length);
	}
}
