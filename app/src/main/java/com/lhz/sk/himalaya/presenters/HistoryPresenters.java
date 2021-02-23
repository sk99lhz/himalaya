package com.lhz.sk.himalaya.presenters;

import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.data.db.HistoryDao;
import com.lhz.sk.himalaya.data.db.IHistoryDaoCallback;
import com.lhz.sk.himalaya.interfaces.IHistoryPresenter;
import com.lhz.sk.himalaya.interfaces.IHistoryViewCallBack;
import com.lhz.sk.himalaya.utils.Contants;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by song
 */
public class HistoryPresenters implements IHistoryPresenter, IHistoryDaoCallback {
    private static final String TAB ="HistoryPresenters" ;
    private List<IHistoryViewCallBack> callBacks = new ArrayList<>();
    public static HistoryPresenters Instance;
    private final HistoryDao mHistoryDao;
    private List<Track> mCurrentHistory = null;
    private Track mCurrentHistoryTrack = null;

    private HistoryPresenters() {
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistory();
    }

    public static HistoryPresenters getInstance() {
        if (Instance == null) {
            synchronized (HistoryPresenters.class) {
                if (Instance == null)
                    Instance = new HistoryPresenters();
            }
        }
        return Instance;
    }

    @Override
    public void listHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null)
                    mHistoryDao.getHistorys();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isOutOfSize = false;

    @Override
    public void addHistory(Track track) {
        this.mCurrentHistoryTrack = track;
        if (mCurrentHistory != null && mCurrentHistory.size() >= Contants.MAX_HISTORY_COUNT) {
            delHistory(mCurrentHistory.get(mCurrentHistory.size() - 1));
            isOutOfSize = true;
        } else {
            doAddHistory(track);
        }

    }

    private void doAddHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null)
                    mHistoryDao.addHistory(track);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null)
                    mHistoryDao.delHistory(track);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null)
                    mHistoryDao.clearHistory();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryViewCallBack callBack) {
        if (!callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(IHistoryViewCallBack callBack) {
        callBacks.remove(callBack);
    }

    @Override
    public void onAddHistory(boolean isOk) {
        listHistory();
    }

    @Override
    public void onDelHistory(boolean isOk) {
        if (isOutOfSize && mCurrentHistoryTrack != null) {
            addHistory(mCurrentHistoryTrack);
            isOutOfSize = false;
        } else {
            listHistory();
        }

    }

    @Override
    public void onClearHistory(boolean isOk) {

    }

    @Override
    public void onHistoryLoader(List<Track> tracks) {
        LogUtil.e(TAB,tracks.size()+"");
        this.mCurrentHistory = tracks;
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryViewCallBack callBack : callBacks) {
                    callBack.HistoryLoaded(tracks);
                }
            }
        });
    }
}
