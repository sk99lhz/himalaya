package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.interfaces.IBingCallBack;
import com.lhz.sk.himalaya.interfaces.IBingPresenters;
import com.lhz.sk.himalaya.net.INetCallBack;
import com.lhz.sk.himalaya.net.MyNetUpdater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class BingPresenter implements IBingPresenters {
    private List<IBingCallBack> mCallBacks = new ArrayList<>();

    private BingPresenter() {
    }

    public static BingPresenter instance;

    public static BingPresenter getInstance() {
        if (instance == null) {
            synchronized (BingPresenter.class) {
                if (instance == null) {
                    instance = new BingPresenter();
                }
            }
        }

        return instance;
    }

    @Override
    public void loadBingData() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        MyNetUpdater.getInstance().getNetManger().get(
                requestBingPic, new INetCallBack() {
                    @Override
                    public void success(String response) {
                        if (mCallBacks != null) {
                            for (IBingCallBack callBack : mCallBacks) {
                                callBack.IBingData(response);
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        if (mCallBacks != null) {
                            for (IBingCallBack callBack : mCallBacks) {
                                callBack.IBingFailed(throwable);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void registerViewCallback(IBingCallBack callBack) {
        if (!mCallBacks.contains(callBack)) {
            mCallBacks.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(IBingCallBack callBack) {
        mCallBacks.remove(callBack);
    }
}
