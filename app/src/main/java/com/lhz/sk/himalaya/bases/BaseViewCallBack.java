package com.lhz.sk.himalaya.bases;

import com.lhz.sk.himalaya.interfaces.IRecommendViewCallBack;

/**
 * Created by song
 */
public interface BaseViewCallBack<T> {

    void registerViewCallback(T callBack);
    void unregisterViewCallback(T callBack);
}
