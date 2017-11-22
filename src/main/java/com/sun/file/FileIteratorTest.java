package com.sun.file;

import java.io.File;

/**
 * 测试文件遍历
 * Created by sun on 2017/11/10 上午9:58.
 */
public class FileIteratorTest {



	public static final void recursiveIterator(File file){
		if(file.isDirectory()){
			File[] fileList = file.listFiles();
			for(File fi: fileList){
				System.out.println(fi.lastModified());
				recursiveIterator(fi);
			}
		}else{
			System.out.println(file.getName());
		}
	}

	public static void main(String[] args) {
		//1510309495000
		//1510309527000
		File file = new File("/root/test");
		recursiveIterator(file);
		System.exit(1);
	}
}
