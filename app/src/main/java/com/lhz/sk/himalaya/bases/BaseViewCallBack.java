package com.lhz.sk.himalaya.bases;



/**
 * Created by song
 */
public interface BaseViewCallBack<T> {

    void registerViewCallback(T callBack);
    void unregisterViewCallback(T callBack);
}
