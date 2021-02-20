package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.interfaces.IRecommendPresenters;
import com.lhz.sk.himalaya.interfaces.IRecommendViewCallBack;
import com.lhz.sk.himalaya.utils.Contants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by song
 */
public class RecommendPresenter implements IRecommendPresenters {
    public static RecommendPresenter Instance = null;
    private List<IRecommendViewCallBack> callBacks = new ArrayList<>();

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
        upLoading();
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Contants.RD_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList.size() == 0) {
                        if (callBacks != null) {
                            for (IRecommendViewCallBack callBack : callBacks) {
                                callBack.onEmpty();
                            }
                        }
                    } else {
                        if (callBacks != null) {
                            for (IRecommendViewCallBack callBack : callBacks) {
                                callBack.onRecommendListData(albumList);
                            }
                        }
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

    private  void  upLoading(){
        for (IRecommendViewCallBack callBack : callBacks) {
            callBack.onLoading();
        }
    }

}
