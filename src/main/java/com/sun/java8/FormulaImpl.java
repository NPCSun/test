package com.sun.java8;

/**
 * Created by sun on 2017/12/26 下午1:44.
 */
public class FormulaImpl implements Formula {
	@Override
	public double calculate(int a) {
		return 0;
	}

	public static void main(String[] args) {
		Formula formula = new FormulaImpl();
		//System.out.println(formula.sqrt(16));
	}
}
