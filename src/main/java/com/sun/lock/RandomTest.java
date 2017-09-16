package com.sun.lock;

import java.util.Random;

public class RandomTest {

	public static void main(String[] args) {
		Random random = new Random();
		int count = 0;
		int randCount = 0;
		while(true){
			if(count == 20){
				break;
			}
			while(randCount<50){
				randCount = random.nextInt(100);
			}
			System.out.println(randCount + random.nextInt(50));
			count++;
		}

	}

}
