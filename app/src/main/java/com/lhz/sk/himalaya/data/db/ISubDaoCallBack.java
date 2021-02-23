package com.lhz.sk.himalaya.data.db;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by song
 */
public interface ISubDaoCallBack {
    void onAddResult(boolean isSuccess);

    void onDeleteResult(boolean isSuccess);

    void OnSubListLoaded(List<Album> result);
}
