package com.sun.extend;

/**
 * Created by sun on 2017/12/21 下午4:07.
 */
public class Parent {
	private transient String name = "parent";

	public String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}
}
