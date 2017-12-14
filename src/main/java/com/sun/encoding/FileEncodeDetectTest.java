package com.sun.encoding;

/**
 * Created by sun on 2017/11/25 下午6:04.
 */
public class FileEncodeDetectTest {
	public static void main(String[] args) {
		String fileEncode = EncodingDetect.getJavaEncode("/root/antx.properties");
		System.out.println(fileEncode);
	}
}
