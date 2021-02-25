package com.lhz.sk.himalaya.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by song
 */
public class JsonDataUtils {
    /**
     * 读取资源文件中的数据
     *
     * @return
     */
    public static String getJsonFromAssets(Context context, String fileName) {
//        StringBuilder 存放读取出的数据
        StringBuilder stringBuilder = new StringBuilder();
//        AssetManager 资源管理器
        AssetManager assetManager = context.getAssets();
//        Open 方法 打开指定的资源文件，返回InputStream 输入流
        try {
            InputStream inputStream = assetManager.open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            循环利用 BufferedReader 的 readLine 方法读取每一行的数据
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();

    }
}
