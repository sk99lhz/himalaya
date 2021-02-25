package com.lhz.sk.himalaya.utils;

/**
 * Created by song
 */
public class Message {
    public boolean isPlay;

    public Message(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}
