package com.sun.security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SecurityManagerTest {

	public static void main(String[] args){
		System.out.println("SecurityManager: " + System.getSecurityManager());
		
		System.out.println(System.getProperty("file.encoding"));
        try {
			FileInputStream fis = new FileInputStream("d:\\Readme.txt");
			/*byte[] result = new byte[1024];
			while(fis.read(result)!=-1){
				String str = new String(result);
				System.out.println(str);
			}*/
			System.out.println(fis.available());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}

}
