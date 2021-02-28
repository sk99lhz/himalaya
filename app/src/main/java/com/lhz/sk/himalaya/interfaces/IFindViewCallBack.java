package com.lhz.sk.himalaya.interfaces;

import android.text.TextUtils;

import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.List;

/**
 * Created by song
 */
public interface IFindViewCallBack {
    void LoadedFindView(List<Announcer> announcerList);

    void onError(int errCode, String errMsg);

    void Empty();

    void onLoading();
}
