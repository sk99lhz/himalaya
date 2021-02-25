package com.lhz.sk.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lhz.sk.himalaya.data.api.MyXimalayaApi;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.interfaces.IPlayerPresenter;
import com.lhz.sk.himalaya.interfaces.IPlayerViewCallBack;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.lhz.sk.himalaya.utils.ToastUtils;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

/**
 * Created by song
 */
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {
    public static PlayerPresenter Instance = null;
    private List<IPlayerViewCallBack> callBacks = new ArrayList<>();
    private String TAB = "PlayerPresenter";
    private XmPlayerManager mPlayerManager;
    private boolean mPlayListSet = false;
    private Track mTrack;
    private int mCurrentIndex = 0;
    private final SharedPreferences mPlayMode;
    private boolean isReverse = false;
    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;
    private List<Track> mCurrentTrack = new ArrayList<>();
    public static final String PLAY_MODE_SP_NAME = "PLAY_MODE_SP_NAME";

    public static final String PLAY_MODE_SP_KEY = "PLAY_MODE_SP_KEY";

    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;
    private int mCurrentPosition = 0;
    private int mCurrentDuration = 0;

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch (mode) {
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
        }

        return PLAY_MODEL_LIST_INT;
    }

    public static PlayerPresenter getInstance() {
        if (Instance == null) {
            synchronized (PlayerPresenter.class) {
                if (Instance == null) {
                    Instance = new PlayerPresenter();
                }
            }
        }
        return Instance;
    }



    public void setPlayList(List<Track> tracks, int index) {
        if (mPlayerManager != null) {
            LogUtil.e(TAB, "setPlayList= " + tracks.get(index).getPlayUrl32());
            mPlayerManager.setPlayList(tracks, index);
            mPlayListSet = true;
            mTrack = tracks.get(index);
            mCurrentIndex = index;
        } else {
            LogUtil.e(TAB, "setPlayList= null");
        }

    }

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getContext());
        //广告
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器相关接口
        mPlayerManager.addPlayerStatusListener(this);
        mPlayMode = BaseApplication.getContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);
    }


    @Override
    public void play() {
        if (mPlayListSet) {
            mPlayerManager.play();
        }

    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }

    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }

    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void swicthPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayMode(mode);
            mCurrentPlayMode = mode;
            for (IPlayerViewCallBack callBack : callBacks) {
                callBack.onPlayModeChange(mode);
            }
            SharedPreferences.Editor editor = mPlayMode.edit();
            editor.putInt(PLAY_MODE_SP_KEY, getIntByPlayMode(mode));
            editor.apply();
        }
    }


    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            for (IPlayerViewCallBack callBack : callBacks) {
                callBack.onListLoaded(mPlayerManager.getPlayList());
            }
        }

    }

    @Override
    public void playByIndex(int index) {
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }

    }

    @Override
    public void seekTo(int seek) {
        mPlayerManager.seekTo(seek);
    }

    @Override
    public boolean isPlay() {

        return mPlayerManager.isPlaying();
    }

    @Override
    public void revesePlayList() {
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        isReverse = !isReverse;
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList, mCurrentIndex);

        mTrack = (Track) mPlayerManager.getCurrSound();

        for (IPlayerViewCallBack callBack : callBacks) {
            callBack.onListLoaded(playList);
            callBack.onTrackUpData(mTrack, mCurrentIndex);
            callBack.updateListOrder(isReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        MyXimalayaApi ximalayApi = MyXimalayaApi.getInstance();
        ximalayApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && tracks.size() > 0) {
                    mPlayerManager.setPlayList(tracks, 0);
                    mPlayListSet = true;
                    mTrack = tracks.get(0);
                    mCurrentIndex = 0;
                    for (IPlayerViewCallBack callBack : callBacks) {
                        callBack.onListLoaded(trackList.getTracks());
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                ToastUtils.showToast(BaseApplication.getContext(), i + s);
            }
        }, id, 1);
    }


    @Override
    public void registerViewCallback(IPlayerViewCallBack callBack) {
        if (!callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
        getPlayList();
        callBack.onTrackUpData(mTrack, mCurrentIndex);
        callBack.onProgressChange(mCurrentPosition, mCurrentDuration);
        handelPalyStae(callBack);
        int anInt = mPlayMode.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getModeByIntPlay(anInt);
        callBack.onPlayModeChange(mCurrentPlayMode);
    }

    private void handelPalyStae(IPlayerViewCallBack callBack) {
        int status = mPlayerManager.getPlayerStatus();
        if (PlayerConstants.STATE_STARTED == status) {
            callBack.onPlayStart();
        } else {
            callBack.onPlayPause();
        }
    }

    private XmPlayListControl.PlayMode getModeByIntPlay(int index) {
        switch (index) {
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;

            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;

        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void unregisterViewCallback(IPlayerViewCallBack callBack) {
        if (callBacks != null) {
            callBacks.remove(callBack);
        }
    }

    //======================广告相关回调==========
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.e(TAB, "onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.e(TAB, "onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.e(TAB, "onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.e(TAB, "onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.e(TAB, "onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.e(TAB, "onCompletePlayAds");
    }

    @Override
    public void onError(int i, int i1) {
        LogUtil.e(TAB, "onError");
    }
    //======================广告相关回调==========


    //播放器相关接口监听
    @Override
    public void onPlayStart() {
        for (IPlayerViewCallBack callBack : callBacks) {
            callBack.onPlayStart();
        }

    }

    @Override
    public void onPlayPause() {
        for (IPlayerViewCallBack callBack : callBacks) {
            callBack.onPlayPause();
        }

    }

    @Override
    public void onPlayStop() {
        for (IPlayerViewCallBack callBack : callBacks) {
            callBack.onPlayStop();
        }

    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track track = (Track) curModel;
            this.mTrack = track;
            HistoryPresenters historyPresenters = HistoryPresenters.getInstance();
            historyPresenters.addHistory(track);
            if (callBacks != null) {
                for (IPlayerViewCallBack callBack : callBacks) {
                    callBack.onTrackUpData(track, mCurrentIndex);
                }
            }
        }

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int i) {

    }

    @Override
    public void onPlayProgress(int i, int i1) {
        this.mCurrentPosition = i;
        this.mCurrentDuration = i1;
        for (IPlayerViewCallBack callBack : callBacks) {
            callBack.onProgressChange(i, i1);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {

        return false;
    }


    public boolean isPlayList() {
        return mPlayListSet;
    }
}
