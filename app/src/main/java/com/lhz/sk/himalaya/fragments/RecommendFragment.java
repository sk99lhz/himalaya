package com.lhz.sk.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lhz.sk.himalaya.activitys.DetailsActivity;
import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.adapters.AlbumListAdapter;
import com.lhz.sk.himalaya.bases.BaseFragment;
import com.lhz.sk.himalaya.interfaces.IRecommendViewCallBack;
import com.lhz.sk.himalaya.presenters.DetailPresenter;
import com.lhz.sk.himalaya.presenters.RecommendPresenter;
import com.lhz.sk.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by song
 */
public class RecommendFragment extends BaseFragment implements IRecommendViewCallBack, UILoader.OnRetryClickListener, AlbumListAdapter.onAlbumItemCallLister {
    private String TAB = "RecommendFragment";
    private View mView;
    private RecyclerView mRecommendList;
    AlbumListAdapter mRecommendListAdapter;
    RecommendPresenter mRecommendPresenter;
    UILoader mUILoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {

        if (mUILoader==null){
            mUILoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup view) {
                    return creatSuccesView(inflater, view);
                }
            };
        }
        mRecommendListAdapter = new AlbumListAdapter();
        mRecommendList.setAdapter(mRecommendListAdapter);
        mRecommendPresenter = RecommendPresenter.getInstance();
        // getRecommendData();
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendData();
        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }
        mUILoader.setOnRetryClickListener(this);
        mRecommendListAdapter.setonAlbumItemCallLister(this);
        return mUILoader;
    }

    private View creatSuccesView(LayoutInflater inflater, ViewGroup container) {
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
                outRect.left = UIUtil.dip2px(getContext(), 5);
                outRect.right = UIUtil.dip2px(getContext(), 5);
            }
        });
        TwinklingRefreshLayout refreshLayout = mView.findViewById(R.id.over_scroll_view);
        refreshLayout.setPureScrollModeOn();
        return mView;
    }


    private void upRecommendUi(List<Album> albumList) {
        mRecommendListAdapter.setData(albumList);
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onRecommendListData(List<Album> albums) {
        upRecommendUi(albums);
    }

    @Override
    public void onNetworkError() {
        mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unregisterViewCallback(this);
        }

    }

    @Override
    public void OnRetry() {
        if (mRecommendPresenter != null) {
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
            mRecommendPresenter.getRecommendData();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        DetailPresenter presenter = DetailPresenter.getInstance();
        presenter.setTargetAlbum(album);
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        startActivity(intent);
    }
}
