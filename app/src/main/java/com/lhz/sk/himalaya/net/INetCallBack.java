package com.lhz.sk.himalaya.net;

/**
 * Created by song
 */
public interface INetCallBack {
    void success(String response);

    void failed(Throwable throwable);

}
