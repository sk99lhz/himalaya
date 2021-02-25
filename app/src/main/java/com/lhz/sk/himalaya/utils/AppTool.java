package com.lhz.sk.himalaya.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.audiofx.AudioEffect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lhz.sk.himalaya.bases.BaseApplication;

public class AppTool {
    /**
     * 动态申请权限
     * @param context    上下文
     * @param permission 要申请的一个权限，列如写的权限：Manifest.permission.WRITE_EXTERNAL_STORAGE
     * @return  是否有当前权限
     */

    public static boolean RequestPermissions(@NonNull Context context, @NonNull String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.i("requestMyPermissions",": 【 " + permission + " 】没有授权，申请权限");
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 100);
            return false;
        } else {
            Log.i("requestMyPermissions",": 【 " + permission + " 】有权限");
            return true;
        }
    }
    public static boolean isAudioControlPanelAvailable(Context context) {
        return isIntentAvailable(context, new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL));
    }
    private static boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER) != null;
    }
    public static String getVersionName()
    {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = BaseApplication.getContext().getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(BaseApplication.getContext().getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }
}
