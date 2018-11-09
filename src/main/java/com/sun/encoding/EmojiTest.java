package com.sun.encoding;

/**
 * Created by sun on 2017/7/27 下午9:51.
 */
public class EmojiTest {


	public static void main(String[] args) {
		int emojiInt = 0x1F1FF;
		String emojiStr = new String(Character.toChars(emojiInt));
		System.out.println(emojiStr);

        String cs = "韩";
        int length = cs.length();
        System.out.println("emoji length " + length);//2
        int cpCount =cs.codePointCount(0, cs.length());
        System.out.println("cs codePoint count:" + cpCount);

        String chinese = "伔a汉";
        System.out.println("length:" + chinese.length());
        for(int i=0;  i<chinese.length(); i++){
            char ch = chinese.charAt(i);
            int codePoint = chinese.codePointAt(i);
            System.out.println("char-int:" + (int)ch );
            System.out.println("codePoint:" + codePoint);
            System.out.println("char:" + (char) codePoint);
        }
        System.out.println("-------------------------");
	}
}
