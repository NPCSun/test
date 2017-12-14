package com.sun.encoding;

/**
 * Created by sun on 2017/11/25 上午10:00.
 */
public class EncodingTest {

	private static char[] HEX_CHAR = {'0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public static void main(String[] args) throws Exception {

		String[] strArr = {"中", "䲥", "a", "aa"};
		//String[] charsetArr = {"gbk", "utf-8", "utf-16", "gb2312"};
		String[] charsetArr = {"unicode", "utf-8", "utf-16", "utf-16BE", "utf-16LE"};
		for (String str : strArr) {
			System.out.println(str);
			for (String charset : charsetArr) {
				byteTest(str, charset);
			}
			System.out.println("============================");
		}
	}

	public static void byteTest(String str, String charset) throws Exception {
		byte[] strByte = str.getBytes(charset);
		System.out.println("编码：" + charset
				+ "\t所占字节数：" + strByte.length
				+ "\t16进制：" + bytesToHexStr(strByte));
	}

	// 将byte[]用十六进制字符串
	public static String bytesToHexStr(byte[] bytes) {
		int index = 0;
		char[] hexChar = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			hexChar[index++] = HEX_CHAR[bytes[i] >> 4 & 0xF];
			hexChar[index++] = HEX_CHAR[bytes[i] & 0xF];
		}
		return new String(hexChar);
	}

}