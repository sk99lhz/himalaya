package com.lhz.sk.himalaya.net;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by song
 */
public class OkhttpNetManager implements INetManger {
    private static OkHttpClient IOkHttpClient;

    //private static Handler mHandler= new Handler(Looper.getMainLooper());
    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.callTimeout(15, TimeUnit.SECONDS);
        IOkHttpClient = builder.build();
    }

    @Override
    public void get(String url, INetCallBack callBack) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().build();
        Call call = IOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                BaseApplication.getHandler().post(() -> callBack.failed(e));

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String string = response.body().string();
                    BaseApplication.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(string);
                            LogUtil.e(OkhttpNetManager.class.getName()+" ",Thread.currentThread().getName());
                        }
                    });


                } catch (Throwable throwable) {
                    BaseApplication.getHandler().post(() -> callBack.failed(throwable));

                }

            }
        });
    }

    @Override
    public void download(String url, File targetFile, INetDownloadCallBack callBack) {
        if (!targetFile.exists()) {
            targetFile.getParentFile().mkdirs();
        }
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().build();
        Call call = IOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.failed(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream is = null;
                OutputStream os = null;
                try {

                    long totallen = response.body().contentLength();
                    is = response.body().byteStream();
                    os = new FileOutputStream(targetFile);
                    byte[] buffer = new byte[8 * 1024];
                    long curlen = 0;
                    int bufferlen = 0;
                    while ((bufferlen = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bufferlen);
                        os.flush();
                        curlen += bufferlen;
                        long finalCurlen = curlen;
                        callBack.progress((int) (finalCurlen * 1.0f / totallen * 100));
                    }

                    try {
                        targetFile.setExecutable(true, false);
                        targetFile.setReadable(true, false);
                        targetFile.setWritable(true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    callBack.success(targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                }

            }
        });
    }

    /**
     * 通知媒体库更新文件
     *
     * @param context
     * @param filePath 文件全路径
     */
    public void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // scanIntent.setData(Uri.parse(filePath));
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

}
