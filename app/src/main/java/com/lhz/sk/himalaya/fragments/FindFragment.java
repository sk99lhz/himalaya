package com.lhz.sk.himalaya.fragments;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.activitys.DetailsActivity;
import com.lhz.sk.himalaya.adapters.FindListAdapter;
import com.lhz.sk.himalaya.bases.BaseFragment;
import com.lhz.sk.himalaya.interfaces.IBingPresenters;
import com.lhz.sk.himalaya.interfaces.IFindPresenters;
import com.lhz.sk.himalaya.interfaces.IFindViewCallBack;
import com.lhz.sk.himalaya.presenters.DetailPresenter;
import com.lhz.sk.himalaya.presenters.FindPresenters;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.lhz.sk.himalaya.utils.ToastUtils;
import com.lhz.sk.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategory;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategoryList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindFragment extends BaseFragment implements IFindViewCallBack, FindListAdapter.onFindListItemCallLister {

    private View mView;
    private RecyclerView mRecyclerView;
    private FindListAdapter mFindListAdapter;
    private IFindPresenters mFindPresenters;
    UILoader mUILoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {

        if (mUILoader == null) {
            mUILoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup group) {
                    return creatSuccesView(inflater, group);
                }
            };
        }
        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void OnRetry() {
                mFindPresenters.getFindData();
            }
        });
        return mUILoader;
    }

    private View creatSuccesView(LayoutInflater inflater, ViewGroup group) {
        mView = inflater.inflate(R.layout.fragment_find, group, false);
        mRecyclerView = mView.findViewById(R.id.find_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(group.getContext(), 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mFindListAdapter = new FindListAdapter();
        mFindListAdapter.setonFindListItemCallLister(this);
        mRecyclerView.setAdapter(mFindListAdapter);
        mFindPresenters = FindPresenters.getInstance();
        mFindPresenters.registerViewCallback(this);
        mFindPresenters.getFindData();
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(getContext(), 5);
                outRect.bottom = UIUtil.dip2px(getContext(), 5);
                outRect.left = UIUtil.dip2px(getContext(), 5);
                outRect.right = UIUtil.dip2px(getContext(), 5);
            }
        });
        return mView;
    }


    @Override
    public void LoadedFindView(List<Announcer> announcerList) {
        if (announcerList != null) {
            mFindListAdapter.setData(announcerList);
            if (mUILoader != null)
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

    }

    @Override
    public void onError(int errCode, String errMsg) {
        if (mUILoader != null)
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void Empty() {
        if (mUILoader != null)
            mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        if (mUILoader != null)
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onItemClick(int position, Announcer announcer) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.AID, announcer.getAnnouncerId() + "");
        CommonRequest.getAlbumsByAnnouncer(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                DetailPresenter presenter = DetailPresenter.getInstance();
                if (albumList.getAlbums().size() != 0 && albumList != null) {
                    Album album = albumList.getAlbums().get(0);
                    if (album != null) {
                        presenter.setTargetAlbum(album);
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        startActivity(intent);
                    }
                } else {
                    ToastUtils.showToast(getActivity(), "暂无数据！");
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}