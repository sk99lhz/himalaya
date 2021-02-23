package com.lhz.sk.himalaya.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by song
 */
public class ToastUtils {
    public static void showToast(Context context ,String data){
        Toast.makeText(context,data,Toast.LENGTH_SHORT).show();

    }
    public static void showToast(Context context ,int data){
        Toast.makeText(context,data,Toast.LENGTH_SHORT).show();

    }
}
