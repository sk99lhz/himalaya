package com.lhz.sk.himalaya.interfaces;

import com.lhz.sk.himalaya.bases.BaseViewCallBack;

/**
 * Created by song
 */
public interface ISearchPresenter extends BaseViewCallBack<ISearchViewCallBack> {
    void Search(String path);

    void reSearch();

    void loadMore();

    void getHotWord();

    void getRecommendWord(String keyword);
}
