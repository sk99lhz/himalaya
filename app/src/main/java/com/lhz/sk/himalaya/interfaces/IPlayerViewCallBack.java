package com.lhz.sk.himalaya.interfaces;

import android.os.Trace;

import com.lhz.sk.himalaya.bases.BaseViewCallBack;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * Created by song
 */
public interface IPlayerViewCallBack {

    void onPlayStart();

    void onPlayPause();

    void onPlayStop();

    void NextPlay(Track track);

    void onPrePlay();

    void onListLoaded(List<Track> list);

    void onPlayModeChage(XmPlayListControl.PlayMode mode);

    void onProgeressChange(int current, int total);

    void onAdLoading();

    void onAdFinished();


}