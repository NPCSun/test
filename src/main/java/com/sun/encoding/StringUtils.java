package com.sun.encoding;

public class StringUtils {
    public static boolean isEnglish(String charaString){

        return charaString.matches("^[a-zA-Z]*");

    }

    public static void main(String[] args) {
        System.out.println("输入内容是否是纯英文：" + isEnglish( "est"));
    }
}
