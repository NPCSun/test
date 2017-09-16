package com.sun.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NannoTimeTest {

	public static void main(String[] args) {
		/* long startTime = System.nanoTime();
		 long t1 = System.currentTimeMillis();
		 System.out.println(startTime);
		 System.out.println(t1);
		 long estimatedTime = System.nanoTime() - startTime;
		 System.out.println(estimatedTime);*/
		 //System.out.println(System.currentTimeMillis());
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Date date;
		try {
			date = sdf.parse("2016-03-15 17:10:34");
			System.out.println(date.getTime());
			date = sdf.parse("2016-03-16 17:10:34");
			System.out.println(date.getTime());
			date = sdf.parse("2016-03-16 17:11:34");
			System.out.println(date.getTime());
			date = sdf.parse("2016-03-16 17:11:35");
			System.out.println(date.getTime());
			System.out.println(System.currentTimeMillis());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 //1458113299803
	}

}
