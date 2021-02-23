package com.lhz.sk.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by song
 */
public interface IHistoryViewCallBack {
    void HistoryLoaded(List<Track> tracks);
}
