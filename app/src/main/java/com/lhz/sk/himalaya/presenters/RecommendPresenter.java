package com.lhz.sk.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.data.api.MyXimalayaApi;
import com.lhz.sk.himalaya.interfaces.IRecommendPresenters;
import com.lhz.sk.himalaya.interfaces.IRecommendViewCallBack;
import com.lhz.sk.himalaya.utils.JsonDataUtils;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lhz.sk.himalaya.utils.Contants.BINGDATASP;
import static com.lhz.sk.himalaya.utils.Contants.BINGDATASPKEY;

/**
 * Created by song
 */
public class RecommendPresenter implements IRecommendPresenters {
    public static RecommendPresenter Instance = null;
    private List<IRecommendViewCallBack> callBacks = new ArrayList<>();
    private List<Album> mAlbumList = new ArrayList<>();
    private List<Album> mListAlbumList = null;
    private final Album mFromJson;
    private String mBingData;
    private SharedPreferences mBingDataSp;

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
        mBingDataSp = BaseApplication.getContext().getSharedPreferences(BINGDATASP, Context.MODE_PRIVATE);
        mBingData = mBingDataSp.getString(BINGDATASPKEY, "");
        LogUtil.e("mBingData", mBingData);
        String musicSourceJson = JsonDataUtils.getJsonFromAssets(BaseApplication.getContext(), "MyAlbum.json");
        Gson gson = new Gson();
        mFromJson = gson.fromJson(musicSourceJson, Album.class);
        if (mBingData != null)
            mFromJson.setCoverUrlLarge(mBingData);
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
            LogUtil.e("mBingData", "本地=======================");
            if (callBacks != null) {
                for (IRecommendViewCallBack callBack : callBacks) {
                    callBack.onRecommendListData(mListAlbumList);
                }
                mAlbumList = mListAlbumList;
            }

            return;
        }
        upLoading();
        MyXimalayaApi ximalayApi = MyXimalayaApi.getInstance();
        ximalayApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null) {
                    LogUtil.e("mBingData", "网络=======================");
                    mListAlbumList = gussLikeAlbumList.getAlbumList();
                    mListAlbumList.add(mFromJson);
                    if (mListAlbumList.size() == 0) {
                        if (callBacks != null) {
                            for (IRecommendViewCallBack callBack : callBacks) {
                                callBack.onEmpty();
                            }
                        }
                    } else {
                        if (callBacks != null) {
                            Collections.reverse(mListAlbumList);
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
