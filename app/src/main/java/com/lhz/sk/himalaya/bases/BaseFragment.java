package com.lhz.sk.himalaya.bases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by song
 */
public abstract class BaseFragment extends Fragment {
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = onSubViewLoaded(inflater,container);
        return mView;
    }

    protected abstract View onSubViewLoaded(LayoutInflater inflater, ViewGroup container);
}
