package com.sun.extend;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * Created by sun on 2017/12/21 下午4:08.
 */
public class Child extends Parent implements Serializable {

	private String address = "child";



	// deserialize to Object from given file
     public static Object deserialize(String fileName) throws IOException,
             ClassNotFoundException {
		         FileInputStream fis = new FileInputStream(fileName);
		        ObjectInputStream ois = new ObjectInputStream(fis);
		         Object obj = ois.readObject();
		         ois.close();
		         return obj;
		     }

		     // serialize the given object and save it to file
		     public static void serialize(Object obj, String fileName)
             throws IOException {
				 FileOutputStream fos = new FileOutputStream(fileName);
		         ObjectOutputStream oos = new ObjectOutputStream(fos);
		         oos.writeObject(obj);

		         fos.close();
		     }

	public static void main(String[] args) throws IOException, ClassNotFoundException {
     	System.setSecurityManager(new SecurityManager());
		Child child = new Child();
		serialize(child, "/root/a.test");
		child = (Child)deserialize("/root/a.test");
		System.out.println(child.getName());
	}
}
