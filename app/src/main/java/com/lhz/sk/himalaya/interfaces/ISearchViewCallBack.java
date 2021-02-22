package com.lhz.sk.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

/**
 * Created by song
 */
public interface ISearchViewCallBack {

    void onSearchResult(List<Album> albums);

    void onHotWordResult(List<HotWord> hotWordList);

    void onLoadMoreResult(List<Album> result, boolean isOk);

    void onRecommendWordLoaded(List<QueryResult> results);

    void onError(int errCode, String errMsg);
}
