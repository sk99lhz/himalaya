package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.data.db.ISubDaoCallBack;
import com.lhz.sk.himalaya.data.db.SubscriptionDao;
import com.lhz.sk.himalaya.interfaces.IPlayerViewCallBack;
import com.lhz.sk.himalaya.interfaces.ISubscriptionPresenter;
import com.lhz.sk.himalaya.interfaces.ISubscriptionViewCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

import static com.lhz.sk.himalaya.utils.Contants.MAX_SUB_COUNT;

/**
 * Created by song
 */
public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallBack {
    private List<ISubscriptionViewCallBack> callBacks = new ArrayList<>();
    public static SubscriptionPresenter Instance;
    private SubscriptionDao mSubscriptionDao;
    private Map<Long, Album> mData = new HashMap<>();



    private void listSubscription() {
        Observable.create(emitter -> {
            //只调用不处理
            if (mSubscriptionDao != null) {
                mSubscriptionDao.getListAlbum();
            }

        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public static SubscriptionPresenter getInstance() {
        if (Instance == null) {
            synchronized (SubscriptionPresenter.class) {
                if (Instance == null)
                    Instance = new SubscriptionPresenter();
            }
        }
        return Instance;
    }
    private SubscriptionPresenter() {
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallBack(this);
    }

    @Override
    public void addSubscription(Album album) {
        if (mData.size()>=MAX_SUB_COUNT) {
            for (ISubscriptionViewCallBack callBack : callBacks) {
                callBack.onSubToMany();
            }
            return;
        }
        Observable.create(emitter -> {
            if (mSubscriptionDao != null) {
                mSubscriptionDao.addAlbum(album);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(Album album) {
        Observable.create(emitter -> {
            if (mSubscriptionDao != null) {
                mSubscriptionDao.deAlbum(album);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscription() {
        listSubscription();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());
        return result != null;
    }

    @Override
    public void registerViewCallback(ISubscriptionViewCallBack callBack) {
        if (!callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(ISubscriptionViewCallBack callBack) {
        callBacks.remove(callBack);
    }

    @Override
    public void onAddResult(boolean isSuccess) {
        listSubscription();
        BaseApplication.getHandler().post(() -> {
            for (ISubscriptionViewCallBack callBack : callBacks) {
                callBack.onAddResult(isSuccess);
            }
        });
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        listSubscription();
        BaseApplication.getHandler().post(() -> {
            for (ISubscriptionViewCallBack callBack : callBacks) {
                callBack.onDeleteResult(isSuccess);
            }
        });
    }

    @Override
    public void OnSubListLoaded(List<Album> result) {
        mData.clear();
        for (Album album : result) {
            mData.put(album.getId(), album);
        }
        BaseApplication.getHandler().post(() -> {
            for (ISubscriptionViewCallBack callBack : callBacks) {
                callBack.onSubscritpionsLoad(result);
            }
        });
    }
}
