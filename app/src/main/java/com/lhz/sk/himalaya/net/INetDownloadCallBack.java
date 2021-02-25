package com.lhz.sk.himalaya.net;

import java.io.File;

/**
 * Created by song
 */
public interface INetDownloadCallBack {
    void success(File file);

    void progress(int progress);

    void failed(Throwable throwable);
}
