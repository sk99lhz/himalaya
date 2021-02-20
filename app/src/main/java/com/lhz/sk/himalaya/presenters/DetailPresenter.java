package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.interfaces.IDetailPresenters;
import com.lhz.sk.himalaya.interfaces.IDetailViewCallBack;
import com.lhz.sk.himalaya.interfaces.IRecommendViewCallBack;
import com.lhz.sk.himalaya.utils.Contants;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by song
 */
public class DetailPresenter implements IDetailPresenters {

    public static DetailPresenter Instance = null;
    private List<IDetailViewCallBack> callBacks = new ArrayList<>();
    private Album mAlbum;
    private String TAB = "DetailPresenter";

    public static DetailPresenter getInstance() {
        if (Instance == null) {
            synchronized (DetailPresenter.class) {
                if (Instance == null) {
                    Instance = new DetailPresenter();
                }
            }
        }
        return Instance;
    }

    private DetailPresenter() {
    }

    public void setTargetAlbum(Album album) {
        this.mAlbum = album;
    }

    @Override
    public void getRecommendData() {

    }

    @Override
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Contants.COUNT_DETAILS + "");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    if (callBacks != null) {
                        for (IDetailViewCallBack callBack : callBacks) {
                            callBack.onDetailLoaded(trackList.getTracks());
                        }
                    }
                }

            }

            @Override
            public void onError(int i, String s) {
                LogUtil.e(TAB, s + i);
            }
        });
    }

    @Override
    public void registerViewCallback(IDetailViewCallBack callBack) {
        if (!callBacks.contains(callBack) && callBacks != null) {
            callBacks.add(callBack);
            if (mAlbum != null) {
                callBack.onAlbumLoaded(mAlbum);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IDetailViewCallBack callBack) {
        if (!callBacks.contains(callBack) && callBacks != null) {
            callBacks.add(callBack);
        }
    }
}
