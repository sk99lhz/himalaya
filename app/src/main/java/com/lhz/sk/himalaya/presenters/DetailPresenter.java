package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.api.XimalayApi;
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
    private int mCurrentAlbumId = -1;
    private int mCurrentPage = 0;
    private List<Track> mTracks = new ArrayList<>();

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
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {
        mCurrentPage++;
        doLaded(true);
    }

    private void doLaded(boolean isdoLaded) {
        XimalayApi ximalayApi=XimalayApi.getInstance();
        ximalayApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    if (callBacks != null) {
                        if (isdoLaded) {
                            mTracks.addAll(trackList.getTracks());
                            for (IDetailViewCallBack callBack : callBacks) {
                                callBack.onLoadedFinshed(trackList.getTracks().size());
                            }
                        } else {
                            mTracks.addAll(0, trackList.getTracks());
                        }

                        for (IDetailViewCallBack callBack : callBacks) {
                            callBack.onDetailLoaded(mTracks);
                        }
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isdoLaded) {
                    mCurrentPage--;
                }
                if (callBacks != null) {
                    for (IDetailViewCallBack callBack : callBacks) {
                        callBack.onNetworkError(errorCode, errorMsg);
                    }
                }
            }
        }, mCurrentAlbumId, mCurrentPage);
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPage = page;
        doLaded(false);
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
