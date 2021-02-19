package com.lhz.sk.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by song
 */
public interface IRecommendViewCallBack {
    void onRecommendListData(List<Album> albums);

    void onRecommendRefreshMore(List<Album> albums);

    void onRecommendLoadMore(List<Album> albums);
}