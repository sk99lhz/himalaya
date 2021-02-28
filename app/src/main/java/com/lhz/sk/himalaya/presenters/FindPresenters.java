package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.data.api.MyXimalayaApi;
import com.lhz.sk.himalaya.interfaces.IBingCallBack;
import com.lhz.sk.himalaya.interfaces.IFindPresenters;
import com.lhz.sk.himalaya.interfaces.IFindViewCallBack;
import com.lhz.sk.himalaya.interfaces.IRecommendViewCallBack;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class FindPresenters implements IFindPresenters {
    public static FindPresenters instance = null;
    private List<IFindViewCallBack> mCallBacks = new ArrayList<>();


    private FindPresenters() {
    }

    public static FindPresenters getInstance() {
        if (instance == null) {
            synchronized (FindPresenters.class) {
                if (instance == null) {
                    instance = new FindPresenters();
                }
            }
        }

        return instance;
    }

    @Override
    public void getFindData() {
        upLoading();
        FindData("0");
        FindData("2");
        FindData("5");
        FindData("20");
        FindData("61");
    }

    private void upLoading() {
        if (mCallBacks != null) {
            for (IFindViewCallBack callBack : mCallBacks) {
                callBack.onLoading();
            }
        }

    }

    private void FindData(String data) {
        MyXimalayaApi ximalayaApi = MyXimalayaApi.getInstance();
        ximalayaApi.getFindData(new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(AnnouncerList announcerList) {
                List<Announcer> announcers = announcerList.getAnnouncerList();
                if (announcers.size() == 0 || announcers == null) {
                    if (mCallBacks != null) {
                        for (IFindViewCallBack callBack : mCallBacks) {
                            callBack.Empty();
                        }
                    }

                } else {
                    if (mCallBacks != null) {
                        for (IFindViewCallBack callBack : mCallBacks) {
                            callBack.LoadedFindView(announcers);
                        }
                    }

                }
            }

            @Override
            public void onError(int i, String s) {
                if (mCallBacks != null) {
                    for (IFindViewCallBack callBack : mCallBacks) {
                        callBack.onError(i, s);
                    }
                }
            }
        }, data);
    }

    @Override
    public void registerViewCallback(IFindViewCallBack callBack) {
        if (!mCallBacks.contains(callBack)) {
            mCallBacks.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(IFindViewCallBack callBack) {
        mCallBacks.remove(callBack);
    }
}
