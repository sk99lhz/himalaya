package com.lhz.sk.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by song
 */
public interface IDetailViewCallBack {
    void onDetailLoaded(List<Track> tracks);

    void onAlbumLoaded(Album album);

    void onNetworkError(int errorCode, String errorMsg);

    void onLoadedFinshed(int size);

    void onRefreshFinshed(int size);
}
