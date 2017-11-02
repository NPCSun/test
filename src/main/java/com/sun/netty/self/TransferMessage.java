package com.sun.netty.self;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Created by sun on 2017/9/26 上午11:12.
 */
public class TransferMessage implements Serializable{

	private byte type; //消息类型 0，心跳包；1，业务包

	private int length; //消息长度

	private String body; //对象json序列化后的字符串

	public TransferMessage(){}
	/**
	 *  read时用
	 * @param type
	 * @param length
	 * @param body
	 */
	TransferMessage(byte type, int length, String body){
		this.body = body;
		this.length = length;
		this.type = type;
	}

	/**
	 * write时用
	 * @param type
	 * @param body
	 */
	public TransferMessage(byte type, String body){
		this.body = body;
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public static void main(String[] args) {
		System.out.println(1<<2);
	}
}
