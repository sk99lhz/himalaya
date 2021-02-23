package com.lhz.sk.himalaya.data.db;

import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by song
 */
public interface ISubDao {
    void setCallBack(ISubDaoCallBack callBack);

    void addAlbum(Album album);

    void deAlbum(Album album);

    void getListAlbum();
}
