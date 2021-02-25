package com.lhz.sk.himalaya.net;

/**
 * Created by song
 */
public class MyNetUpdater {
    private static MyNetUpdater sInstance = new MyNetUpdater();

    public static MyNetUpdater getInstance() {
        return sInstance;
    }

    private INetManger mNetManger = new OkhttpNetManager();

    public INetManger getNetManger() {
        return mNetManger;
    }
}
