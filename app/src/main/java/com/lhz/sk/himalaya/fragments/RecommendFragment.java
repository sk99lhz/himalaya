package com.lhz.sk.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.bases.BaseFragment;

/**
 * Created by song
 */
public class RecommendFragment extends BaseFragment {
    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        View inflate = inflater.inflate(R.layout.fragment_rd, container,false);
        return inflate;
    }
}
