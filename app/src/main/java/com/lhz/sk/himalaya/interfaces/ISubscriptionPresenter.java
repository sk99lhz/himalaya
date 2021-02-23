package com.lhz.sk.himalaya.interfaces;

import com.lhz.sk.himalaya.bases.BaseViewCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by song
 */
public interface ISubscriptionPresenter extends BaseViewCallBack<ISubscriptionViewCallBack> {
    void addSubscription(Album album);

    void deleteSubscription(Album album);

    void getSubscription();

    boolean isSub(Album album);
}
