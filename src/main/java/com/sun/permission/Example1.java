package com.sun.permission;

import java.io.File;
import java.io.FilePermission;
import java.security.Permission;

/**
 * Created by sun on 2017/12/21 下午5:59.
 */
class Example1 {
	public static void main(String[] args) {
		char sep = File.separatorChar;
		// Read permission for "/tmp/f"
		Permission file = new FilePermission(sep + "tmp" + sep + "f", "read");
		// Read permission for "/tmp/*", which
		// means all files in the /tmp directory
		// (but not any files in subdirectories
		// of /tmp)
		Permission star = new FilePermission(sep + "tmp" + sep + "*", "read");
		boolean starImpliesFile = star.implies(file);
		boolean fileImpliesStar = file.implies(star);
		// Prints "Star implies file = true"
		System.out.println("Star implies file = " + starImpliesFile);
		// Prints "File implies star = false"
		System.out.println("File implies star = " + fileImpliesStar);
	}
}