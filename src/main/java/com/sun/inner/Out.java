package com.sun.inner;

//外部类
public class Out {

	private int age = 12;

	// 内部类
	class InnerClass {

		public void print() {

			System.out.println(age);

		}

		public void makeInnerObject() {
			InnerClass ic = new InnerClass();
			ic.print();
		}

	}

	public static void main(String[] args) {
		
	}

}