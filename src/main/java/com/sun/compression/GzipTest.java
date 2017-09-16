package com.sun.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;

import org.apache.zookeeper.common.IOUtils;

public class GzipTest {

	public static void compress(byte[] byteData){
		ByteArrayOutputStream out = null;
		GZIPOutputStream gout = null;
		try{
			System.out.println("byteData.length: " + byteData.length);
			out = new ByteArrayOutputStream();
			gout = new GZIPOutputStream(out);
			gout.write(byteData);
			gout.finish();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			IOUtils.cleanup(null, gout);
		}
		byte[] bytes = null;
		if(out!=null ){
			bytes = out.toByteArray();
			try {
				System.out.println(new String(bytes, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("压缩后：" + bytes.length);
			for(byte sin : bytes){
				System.out.println(sin);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		byte[] bytes;
		try {
			//中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国中国
			bytes = "susussusussusussusussusu中国ssusussusussusussusussususs中国usussusussusussusussusussusu中国ssusussusussusussusussusussusussusus".getBytes("utf-8");
			System.out.println("压缩前：");
			for(byte sin : bytes){
				System.out.println(sin);
			}
			//根据长度选择是否进行压缩，长度超过阈值则走压缩，未超过，不走。
			//压缩之后与原数据长度进行对比，如果比原数据短，则传输压缩后的数据，否则传输原数据。
			if(bytes.length>100){
				compress(bytes);
			}else{
				//
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
