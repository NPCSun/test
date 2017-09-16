package com.sun.emoji;

/**
 * Created by sun on 2017/7/27 下午9:51.
 */
public class EmojiTest {
	public static void main(String[] args) {
		int emojiInt = 0x1F1FF;
		String emojiStr = new String(Character.toChars(emojiInt));
		System.out.println(emojiStr);
	}
}
