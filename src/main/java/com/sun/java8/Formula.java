package com.sun.java8;

/**
 * Created by sun on 2017/12/26 下午1:43.
 */
public interface Formula {
	double calculate(int a);

	default double sqrt(int a) {
		return Math.sqrt(a);
	}
}
