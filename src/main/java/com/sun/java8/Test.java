/*
package com.sun.java8;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * Created by sun on 2017/9/29 上午11:14.
 *//*

public class Test {

	static void stream(List<Person> people) {

		long count = people.parallelStream()
				.filter(person1 -> person1.getName().contains("sun"))//惰性求值筛选
				.count();//及早求值统计

	}

	static void list(List<Person> people) {

		int count = 0;
		for(Person person : people){
			if(person.getName().contains("sun")){
				count++;
			}
		}
	}

	public static void main(String[] args) {
		List<Person> people = new ArrayList<>();

		Person person = new Person();
		for(int i=0; i<10; i++){
			person.setName("sun" + i);
			people.add(person);
		}
		people.add(person);

		long begin = System.currentTimeMillis();

		//stream(people);

		list(people);

		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}


	static class Person {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
*/
