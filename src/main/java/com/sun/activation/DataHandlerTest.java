package com.sun.activation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.DataHandler;


public class DataHandlerTest {
	URL url = null;
	DataHandler dh = null;

	public static void main(String args[]) {
		DataHandlerTest test = new DataHandlerTest();
		String strUrl = "http://www.58shiji.com/upload/keenlyCms/www/201504/07081111ent3.jpg";
		test.setURL(strUrl);
		try {
			test.doit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setURL(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("这不是一个正确的URL。");
			System.exit(1);
		}
	}

	public void doit() throws IOException {
		System.out.print("DataHandler信息创建中....");
		dh = new DataHandler(url);
		System.out.println("...读取并创建完成.");
		System.out.println("这个文件的MimeType信息是: " + dh.getContentType());
		System.out.println("这个文件的名称是 : " + dh.getName());
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = dh.getInputStream();
			System.out.println(dh.getContentType());
			byte[] data = new byte[64];
			int count = -1;
			fos = new FileOutputStream(new File("D:\\a.jpg"));
			if (is != null) {
				while ((count = is.read(data)) != -1) {
					fos.write(data,0,count);
					fos.flush();
				}
				/*fos.write(outStream.toByteArray());
				fos.flush();*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is!=null){
				is.close();
			}
			if(fos!=null){
				fos.close();
			}
		}
	}
}
