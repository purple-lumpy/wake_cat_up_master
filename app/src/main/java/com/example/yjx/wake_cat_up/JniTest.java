package com.example.yjx.wake_cat_up;

/**
 * Created by yjx on 2016-9-28.
 */
public class JniTest {
    //本地方法获得字符串，本地方法有c/c++实现
    public static native int NumberFromC();//嵌入秘密信息到图片 renumber.c
    public static native void extractMsg();//从图片中提取出秘密信息 extraction.c
    static {
        //指定库名，加载动态库需要，需要和build.gradle中指定的库名相一致
        System.loadLibrary("Lib-jpeg");
    }
}
