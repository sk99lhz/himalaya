package com.lhz.sk.himalaya.presenters;

import android.util.Log;

import com.lhz.sk.himalaya.data.api.MyXimalayaApi;
import com.lhz.sk.himalaya.interfaces.IRecommendPresenters;
import com.lhz.sk.himalaya.interfaces.IRecommendViewCallBack;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class RecommendPresenter implements IRecommendPresenters {
    public static RecommendPresenter Instance = null;
    private List<IRecommendViewCallBack> callBacks = new ArrayList<>();
    private List<Album> mAlbumList = new ArrayList<>();
    private List<Album> mListAlbumList = null;
    private String TAB="RecommendPresenter";

    public static RecommendPresenter getInstance() {
        if (Instance == null) {
            synchronized (RecommendPresenter.class) {
                if (Instance == null) {
                    Instance = new RecommendPresenter();
                }
            }
        }
        return Instance;
    }

    private RecommendPresenter() {
    }

    @Override
    public void getRecommendData() {
        getRecommend();
    }

    @Override
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallBack callBack) {
        if (!callBacks.contains(callBack) && callBacks != null) {
            callBacks.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(IRecommendViewCallBack callBack) {
        if (callBacks != null) {
            callBacks.remove(callBack);
        }
    }

    //获取推荐
    private void getRecommend() {
        //TODO  缓存策略
        if (!(mListAlbumList == null || mListAlbumList.size() == 0)) {
            Log.e(TAB,"mListAlbumList  ------  本地") ;
            if (callBacks != null) {
                for (IRecommendViewCallBack callBack : callBacks) {
                    callBack.onRecommendListData(mListAlbumList);
                }
                mAlbumList = mListAlbumList;
            }
        }
        upLoading();
        MyXimalayaApi ximalayApi = MyXimalayaApi.getInstance();
        ximalayApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null) {
                    Log.e(TAB,"mListAlbumList  ------  网络") ;
                    mListAlbumList = gussLikeAlbumList.getAlbumList();
                    if (mListAlbumList.size() == 0) {
                        if (callBacks != null) {
                            for (IRecommendViewCallBack callBack : callBacks) {
                                callBack.onEmpty();
                            }
                        }
                    } else {
                        if (callBacks != null) {
                            for (IRecommendViewCallBack callBack : callBacks) {
                                callBack.onRecommendListData(mListAlbumList);
                            }
                        }
                        mAlbumList = mListAlbumList;
                    }

                }

            }

            @Override
            public void onError(int i, String s) {
                if (callBacks != null) {
                    for (IRecommendViewCallBack callBack : callBacks) {
                        callBack.onNetworkError();
                    }
                }
            }
        });

    }

    private void upLoading() {
        for (IRecommendViewCallBack callBack : callBacks) {
            callBack.onLoading();
        }
    }

    public List<Album> getCurrentRecommend() {
        return mAlbumList;
    }

}
