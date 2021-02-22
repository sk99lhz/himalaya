package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.api.XimalayApi;
import com.lhz.sk.himalaya.interfaces.ISearchPresenter;
import com.lhz.sk.himalaya.interfaces.ISearchViewCallBack;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class SearchPresenter implements ISearchPresenter {
    private List<ISearchViewCallBack> callBacks = new ArrayList<>();

    public static SearchPresenter Instance = null;
    private String mCurrentPath;
    private XimalayApi mXimalayApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private String TAB = "SearchPresenter";

    public static SearchPresenter getInstance() {
        if (Instance == null) {
            synchronized (SearchPresenter.class) {
                if (Instance == null) {
                    Instance = new SearchPresenter();
                }
            }
        }
        return Instance;
    }

    private SearchPresenter() {
        mXimalayApi = XimalayApi.getInstance();
    }

    @Override
    public void Search(String path) {
        this.mCurrentPath = path;
        search(path);
    }

    private void search(String path) {
        mXimalayApi.onSearch(new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                if (searchAlbumList != null) {
                    for (ISearchViewCallBack callBack : callBacks) {
                        callBack.onSearchResult(searchAlbumList.getAlbums());
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                for (ISearchViewCallBack callBack : callBacks) {
                    callBack.onError(i,s);
                }
            }
        }, path, mCurrentPage);
    }

    @Override
    public void reSearch() {
        search(mCurrentPath);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        mXimalayApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    for (ISearchViewCallBack callBack : callBacks) {
                        callBack.onHotWordResult(hotWordList.getHotWordList());
                    }
                    LogUtil.e(TAB, "getHotWords" + hotWordList.getHotWordList().size());
                }
            }

            @Override
            public void onError(int i, String s) {
                for (ISearchViewCallBack callBack : callBacks) {
                    callBack.onError(i,s);
                }
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mXimalayApi.getSuggestWord(new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (suggestWords != null) {
                    LogUtil.e(TAB, "getRecommendWord" + suggestWords.getAlbumList().size());
                }
            }

            @Override
            public void onError(int i, String s) {
                for (ISearchViewCallBack callBack : callBacks) {
                    callBack.onError(i,s);
                }
            }
        }, keyword);
    }

    @Override
    public void registerViewCallback(ISearchViewCallBack callBack) {
        if (!callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(ISearchViewCallBack callBack) {
        callBacks.remove(callBack);
    }
}
