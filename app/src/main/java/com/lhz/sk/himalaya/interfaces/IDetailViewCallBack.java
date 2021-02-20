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
}
