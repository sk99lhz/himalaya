package com.lhz.sk.himalaya.interfaces;

import com.lhz.sk.himalaya.bases.BaseViewCallBack;

/**
 * Created by song
 */
public interface IDetailPresenters extends BaseViewCallBack<IDetailViewCallBack> {

    void pullRefreshMore();

    void loadMore();

    void getAlbumDetail(int albumId, int page);
}
