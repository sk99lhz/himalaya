package com.lhz.sk.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by song
 */
public interface ISubscriptionViewCallBack {
    void onAddResult(boolean isSuccess);

    void onDeleteResult(boolean isSuccess);

    void onSubscritpionsLoad(List<Album> albums);

    void onSubToMany();
}
