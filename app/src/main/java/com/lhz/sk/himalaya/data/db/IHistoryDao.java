package com.lhz.sk.himalaya.data.db;

import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by song
 */
public interface IHistoryDao {

    void setCallback(IHistoryDaoCallback callback);

    void addHistory(Track track);

    void delHistory(Track track);

    void clearHistory();

    void getHistorys();
}
