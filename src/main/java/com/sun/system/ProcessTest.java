package com.sun.system;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import sun.management.VMManagement;

/**
 * Created by sun on 2017/11/10 上午10:19.
 */
public class ProcessTest {

	/**
	 * 获取进程号
	 * @return
	 */
	public static final int jvmPid() {
		try {
			RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
			Field jvm = runtime.getClass().getDeclaredField("jvm");
			jvm.setAccessible(true);
			VMManagement mgmt = (VMManagement) jvm.get(runtime);
			Method pidMethod = mgmt.getClass().getDeclaredMethod("getProcessId");
			pidMethod.setAccessible(true);
			int pid = (Integer) pidMethod.invoke(mgmt);
			return pid;
		} catch (Exception e) {
			return -1;
		}
	}

	public static void main(String[] args) {
		System.out.println(jvmPid());
		System.out.println("___");
	}
}
