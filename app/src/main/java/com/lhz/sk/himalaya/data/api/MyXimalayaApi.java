package com.lhz.sk.himalaya.data.api;

import com.lhz.sk.himalaya.utils.Contants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by song
 */
public class MyXimalayaApi {

    public static MyXimalayaApi Instance;

    private void XimalayApi() {

    }

    public static MyXimalayaApi getInstance() {
        if (Instance == null) {
            synchronized (MyXimalayaApi.class) {
                if (Instance == null)
                    Instance = new MyXimalayaApi();
            }
        }
        return Instance;
    }

    public void getRecommendList(IDataCallBack<GussLikeAlbumList> listIDataCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Contants.RD_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, listIDataCallBack);
    }

    public void getAlbumDetail(IDataCallBack<TrackList> callBack, long mCurrentAlbumId, int mCurrentPage) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, mCurrentAlbumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, mCurrentPage + "");
        map.put(DTransferConstants.PAGE_SIZE, Contants.COUNT_DETAILS + "");
        CommonRequest.getTracks(map, callBack);
    }

    public void onSearch(IDataCallBack<SearchAlbumList> callBack, String path, int page) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, path);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Contants.RD_COUNT + "");
        CommonRequest.getSearchedAlbums(map, callBack);
    }

    public void getHotWords(IDataCallBack<HotWordList> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, String.valueOf(Contants.COUNT_HOT));
        CommonRequest.getHotWords(map, callBack);
    }

    public void getSuggestWord(IDataCallBack<SuggestWords> callBack, String key) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, key);
        CommonRequest.getSuggestWord(map, callBack);
    }
}
