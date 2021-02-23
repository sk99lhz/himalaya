package com.lhz.sk.himalaya.data.db;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by song
 */
public interface IHistoryDaoCallback {
    void onAddHistory(boolean isOk);

    void onDelHistory(boolean isOk);

    void onClearHistory(boolean isOk);

    void onHistoryLoader(List<Track> tracks);
}
