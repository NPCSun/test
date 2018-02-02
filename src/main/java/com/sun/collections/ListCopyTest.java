package com.sun.collections;

import java.util.ArrayList;
import java.util.List;

public class ListCopyTest {

	private static class Student{
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
	public static void main(String[] args) {
		List<Student> lt1 = new ArrayList<Student>();
		Student s1 = new Student();
		s1.setName("s1");
		Student s2 = new Student();
		s2.setName("s2");
		lt1.add(s1);
		lt1.add(s2);
		System.out.println("lt1遍历结果如下：");
		for(Student s : lt1){
			System.out.println(s.getName());
		}
		List<Student> lt2 = new ArrayList<Student>(lt1);
		lt1.remove(0);
		System.out.println("移除元素后，lt1遍历结果如下：");
		for(Student s : lt1){
			System.out.println(s.getName());
		}
		System.out.println("lt2遍历结果如下：");
		for(Student s : lt2){
			System.out.println(s.getName());
		}
	}
}
