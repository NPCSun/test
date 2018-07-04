package com.sun.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelTest {

	public static void main(String[] args) {
		String str = "123";
		FileChannel inChannel = null, outChannel = null; 
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream("E:\\dubbo.xsd");
			inChannel = fis.getChannel();
			fos = new FileOutputStream(new File("E:\\6379-out.conf"));
			outChannel = fos.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			/*ByteBuffer buf = ByteBuffer.allocate(1024);
			buf.put("start\r\n".getBytes("utf-8"));
			while(inChannel.read(buf)!=-1){
				buf.flip();        // Flip buffer
				outChannel.write(buf); 
				buf.clear();
			}*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(outChannel!=null){
				try {
					outChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inChannel!=null){
				try {
					inChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("文件复制完成。");
	}

}
