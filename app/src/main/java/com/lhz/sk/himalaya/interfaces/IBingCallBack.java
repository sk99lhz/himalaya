package com.lhz.sk.himalaya.interfaces;

/**
 * Created by song
 */
public interface IBingCallBack {
    void IBingData(String data);

    void IBingFailed(Throwable throwable);
}
