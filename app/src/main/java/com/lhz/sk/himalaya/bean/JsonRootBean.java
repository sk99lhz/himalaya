package com.lhz.sk.himalaya.bean;

import java.util.List;

/**
 * Created by song
 */
public class JsonRootBean {
    private List<Hot> hot;

    public void setHot(List<Hot> hot) {
        this.hot = hot;
    }

    public List<Hot> getHot() {
        return hot;
    }
}
