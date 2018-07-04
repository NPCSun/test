package com.sun.java8;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class LambdaTest {
	public static void main(String[] args) {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		Collections.sort(names, (a, b) -> {
			System.out.println("aaa");
			return a.compareTo(b);//asc
			//return b.compareTo(a);//desc
		});

		System.out.println(Arrays.deepToString(names.toArray()));
	}
}
