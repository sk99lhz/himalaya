package com.lhz.sk.himalaya.interfaces;

import com.lhz.sk.himalaya.bases.BaseViewCallBack;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by song
 */
public interface IHistoryPresenter extends BaseViewCallBack<IHistoryViewCallBack> {
    void listHistory();
    void addHistory(Track track);

    void delHistory(Track track);

    void  clearHistory();

}
