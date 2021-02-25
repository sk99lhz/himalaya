package com.lhz.sk.himalaya.net;

import java.io.File;

/**
 * Created by song
 */
public interface INetManger {
    void get(String url,INetCallBack callBack);
    void download(String url, File targetFile,INetDownloadCallBack callBack);
}
