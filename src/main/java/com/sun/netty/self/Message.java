package com.sun.netty.self;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by sun on 2017/9/26 上午11:12.
 */
@Data
public class Message implements Serializable{

	/** 消息id*/
	private long id;

	
	private String value;

}
