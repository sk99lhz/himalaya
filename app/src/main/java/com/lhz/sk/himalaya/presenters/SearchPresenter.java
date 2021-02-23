package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.data.api.MyXimalayaApi;
import com.lhz.sk.himalaya.interfaces.ISearchPresenter;
import com.lhz.sk.himalaya.interfaces.ISearchViewCallBack;
import com.lhz.sk.himalaya.utils.Contants;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
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
    private MyXimalayaApi mXimalayApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private String TAB = "SearchPresenter";
    private List<Album> mAlbums = new ArrayList<>();

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
        mXimalayApi = MyXimalayaApi.getInstance();
    }

    @Override
    public void Search(String path) {
        mCurrentPage = DEFAULT_PAGE;
        mAlbums.clear();
        this.mCurrentPath = path;
        search(path);
    }

    private void search(String path) {
        mXimalayApi.onSearch(new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                if (searchAlbumList != null) {
                    mAlbums.addAll(searchAlbumList.getAlbums());
                    if (mIsLoadMore) {
                        for (ISearchViewCallBack callBack : callBacks) {
                            if (searchAlbumList.getAlbums().size() == 0) {
                                callBack.onLoadMoreResult(mAlbums, false);
                            } else {
                                callBack.onLoadMoreResult(mAlbums, true);
                            }

                        }
                        mIsLoadMore = false;
                    } else {
                        for (ISearchViewCallBack callBack : callBacks) {
                            callBack.onSearchResult(mAlbums);
                        }
                    }


                }
            }

            @Override
            public void onError(int i, String s) {
                if (mIsLoadMore) {
                    mCurrentPage--;
                    for (ISearchViewCallBack callBack : callBacks) {
                        callBack.onLoadMoreResult(mAlbums, false);
                    }
                    mIsLoadMore = false;
                } else {
                    for (ISearchViewCallBack callBack : callBacks) {
                        callBack.onError(i, s);
                    }
                }

            }
        }, path, mCurrentPage);
    }

    @Override
    public void reSearch() {
        search(mCurrentPath);
    }

    private boolean mIsLoadMore = false;

    @Override
    public void loadMore() {
        if (mAlbums.size() < Contants.RD_COUNT) {
            for (ISearchViewCallBack callBack : callBacks) {
                callBack.onLoadMoreResult(mAlbums, false);
            }
        } else {
            mIsLoadMore = true;
            mCurrentPage++;
            search(mCurrentPath);
        }

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
                    callBack.onError(i, s);
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
                    for (ISearchViewCallBack callBack : callBacks) {
                        callBack.onRecommendWordLoaded(suggestWords.getKeyWordList());
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                for (ISearchViewCallBack callBack : callBacks) {
                    callBack.onError(i, s);
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
