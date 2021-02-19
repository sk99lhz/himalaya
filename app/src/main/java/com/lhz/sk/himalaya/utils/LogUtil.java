package com.lhz.sk.himalaya.utils;

import android.util.Log;

/**
 * Created by song
 */
public class LogUtil {
    public static String sTAG = "LogUtil";
    //控制是否要输出log
    public static boolean sIsRelease = true;
    public static void d(String TAG, String content) {
        if (sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void v(String TAG, String content) {
        if (sIsRelease) {
            Log.v("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void i(String TAG, String content) {
        if (sIsRelease) {
            Log.i("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void w(String TAG, String content) {
        if (sIsRelease) {
            Log.w("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void e(String TAG, String content) {
        if (sIsRelease) {
            Log.e("[" + sTAG + "]" + TAG, content);
        }
    }
}
