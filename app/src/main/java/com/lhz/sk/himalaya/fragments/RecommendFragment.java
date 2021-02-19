package com.lhz.sk.himalaya.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.adapters.RecommendListAdapter;
import com.lhz.sk.himalaya.bases.BaseFragment;
import com.lhz.sk.himalaya.interfaces.IRecommendViewCallBack;
import com.lhz.sk.himalaya.presenters.RecommendPresenter;
import com.lhz.sk.himalaya.utils.Contants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by song
 */
public class RecommendFragment extends BaseFragment implements IRecommendViewCallBack {
    private String TAB = "RecommendFragment";
    private View mView;
    private RecyclerView mRecommendList;
    RecommendListAdapter mRecommendListAdapter;
    RecommendPresenter mRecommendPresenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.fragment_rd, container, false);

        mRecommendList = mView.findViewById(R.id.rd_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendList.setLayoutManager(linearLayoutManager);
        mRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(getContext(), 3);
                outRect.bottom = UIUtil.dip2px(getContext(), 3);
                outRect.left = UIUtil.dip2px(getContext(), 3);
                outRect.right = UIUtil.dip2px(getContext(), 3);
            }
        });
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendList.setAdapter(mRecommendListAdapter);
        mRecommendPresenter = RecommendPresenter.getInstance();
        // getRecommendData();
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendData();
        return mView;
    }


    private void upRecommendUi(List<Album> albumList) {
        mRecommendListAdapter.setData(albumList);
    }

    @Override
    public void onRecommendListData(List<Album> albums) {
        upRecommendUi(albums);
    }

    @Override
    public void onRecommendRefreshMore(List<Album> albums) {

    }

    @Override
    public void onRecommendLoadMore(List<Album> albums) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unregisterViewCallback(this);
        }

    }
}
