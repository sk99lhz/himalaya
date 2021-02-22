package com.lhz.sk.himalaya.interfaces;

import com.lhz.sk.himalaya.bases.BaseViewCallBack;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

/**
 * Created by song
 */
public interface IPlayerPresenter extends BaseViewCallBack<IPlayerViewCallBack> {
    void play();

    void pause();

    void stop();

    void playPre();

    void playNext();

    void swicthPlayMode(XmPlayListControl.PlayMode mode);

    void getPlayList();

    void playByIndex(int index);

    void seekTo(int seek);

    boolean isPlay();

    void revesePlayList();

    void playByAlbumId(long id);

}
