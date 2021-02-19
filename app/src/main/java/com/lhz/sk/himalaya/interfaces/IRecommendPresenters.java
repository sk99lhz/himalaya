package com.lhz.sk.himalaya.interfaces;

import com.lhz.sk.himalaya.bases.BaseViewCallBack;

/**
 * Created by song
 */
public interface IRecommendPresenters extends BaseViewCallBack<IRecommendViewCallBack> {
    void getRecommendData();

    void pullRefreshMore();

    void loadMore();
}
