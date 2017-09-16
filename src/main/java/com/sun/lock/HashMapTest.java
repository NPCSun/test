package com.sun.lock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HashMapTest {

	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.add(6);
		list.add(7);
		list.add(8);
		Collections.shuffle(list);
		for(int i : list){
			System.out.print(i + "\t");
		}
	}

}
