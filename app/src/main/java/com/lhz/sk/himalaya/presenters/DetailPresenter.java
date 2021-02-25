package com.lhz.sk.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.bean.Hot;
import com.lhz.sk.himalaya.bean.JsonRootBean;
import com.lhz.sk.himalaya.data.api.MyXimalayaApi;
import com.lhz.sk.himalaya.interfaces.IBingCallBack;
import com.lhz.sk.himalaya.interfaces.IBingPresenters;
import com.lhz.sk.himalaya.interfaces.IDetailPresenters;
import com.lhz.sk.himalaya.interfaces.IDetailViewCallBack;
import com.lhz.sk.himalaya.utils.JsonDataUtils;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

import static com.lhz.sk.himalaya.utils.Contants.BINGDATASP;
import static com.lhz.sk.himalaya.utils.Contants.BINGDATASPKEY;

/**
 * Created by song
 */
public class DetailPresenter implements IDetailPresenters {

    public static DetailPresenter Instance = null;
    private List<Track> mFromJson = new ArrayList<>();
    private List<IDetailViewCallBack> callBacks = new ArrayList<>();
    private Album mAlbum;
    private String TAB = "DetailPresenter";
    private int mCurrentAlbumId = -1;
    private int mCurrentPage = 0;
    private List<Track> mTracks = new ArrayList<>();
    private boolean mIslike = false;
    private String mBingData;
    private SharedPreferences mBingDataSp;

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
        mBingDataSp = BaseApplication.getContext().getSharedPreferences(BINGDATASP, Context.MODE_PRIVATE);

        if (!TextUtils.isEmpty(mBingDataSp.getString(BINGDATASPKEY, "")))
            mBingData = mBingDataSp.getString(BINGDATASPKEY, "");
        Track track = new Track();
        String musicSourceJson = JsonDataUtils.getJsonFromAssets(BaseApplication.getContext(), "MyTrack.json");
        Gson gson = new Gson();
        JsonRootBean json = gson.fromJson(musicSourceJson, JsonRootBean.class);
        for (int i = 0; i < json.getHot().size(); i++) {
            if (mBingData != null) {
                track.setCoverUrlLarge(mBingData);
                track.setCoverUrlMiddle(mBingData);
                track.setCoverUrlSmall(mBingData);
            }
            track = json.getHot().get(i);
            track.setKind(PlayableModel.KIND_TRACK);
            track.setPlayUrl32(json.getHot().get(i).getDownloadUrl());
            track.setDataId(-1 * json.getHot().get(i).getTrackTitle().hashCode());
            mFromJson.add(track);
        }
    }

    public void setTargetAlbum(Album album) {
        if (album.getAlbumTitle().equals("不仅仅是喜欢")) {
            this.mIslike = true;
        } else {
            this.mIslike = false;
        }
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
        MyXimalayaApi ximalayApi = MyXimalayaApi.getInstance();
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
                            if (trackList.getTracks().size() > 0) {
                                mTracks.addAll(0, trackList.getTracks());
                            } else {
                                if (mIslike) {
                                    mTracks.addAll(mFromJson);
                                }
                            }
                        }
                        for (IDetailViewCallBack callBack : callBacks) {
                            callBack.onDetailLoaded(mTracks);
                        }
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.e(TAB, "errorCode===" + errorCode + "errorMsg ====" + errorMsg);
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
        if (callBacks != null) {
            callBacks.add(callBack);
        }
    }

}
