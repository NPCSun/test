package com.sun.netty.self;

import java.io.Serializable;

/**
 * Created by sun on 2017/9/26 上午11:12.
 */
public class Message implements Serializable{

	private long id;

	private String value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
