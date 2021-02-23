package com.lhz.sk.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lhz.sk.himalaya.PlayerActivity;
import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.adapters.AlbumListAdapter;
import com.lhz.sk.himalaya.adapters.DetailListAdapter;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.bases.BaseFragment;
import com.lhz.sk.himalaya.interfaces.IHistoryViewCallBack;
import com.lhz.sk.himalaya.presenters.HistoryPresenters;
import com.lhz.sk.himalaya.presenters.PlayerPresenter;
import com.lhz.sk.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by song
 */
public class HistoryFragment extends BaseFragment implements IHistoryViewCallBack, DetailListAdapter.onDetailItemCallLister {

    private UILoader mUiLoader;
    private RecyclerView mRecyclerView;
    private TwinklingRefreshLayout mTwinklingRefreshLayout;
    private DetailListAdapter mDetailListAdapter;
    private HistoryPresenters mHistoryPresenters;


    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        FrameLayout inflate = (FrameLayout) inflater.inflate(R.layout.fragment_hy, container, false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup group) {
                    return createSuccessView(container);
                }
            };

        }
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }
        inflate.addView(mUiLoader);
        return inflate;
    }

    private View createSuccessView(ViewGroup container) {
        View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history_fm, container, false);
        mHistoryPresenters = HistoryPresenters.getInstance();
        mHistoryPresenters.registerViewCallback(this);

        mRecyclerView = inflate.findViewById(R.id.rd_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
        mDetailListAdapter = new DetailListAdapter();
        mRecyclerView.setAdapter(mDetailListAdapter);

        mTwinklingRefreshLayout = inflate.findViewById(R.id.over_scroll_view);
        mTwinklingRefreshLayout.setEnableRefresh(false);
        mTwinklingRefreshLayout.setEnableLoadmore(false);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(getContext(), 3);
                outRect.bottom = UIUtil.dip2px(getContext(), 3);
                outRect.left = UIUtil.dip2px(getContext(), 5);
                outRect.right = UIUtil.dip2px(getContext(), 5);
            }
        });
        mDetailListAdapter.setItemCallLister(this);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return inflate;
    }

    @Override
    public void HistoryLoaded(List<Track> tracks) {
        mDetailListAdapter.setData(tracks);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHistoryPresenters != null) {
            mHistoryPresenters.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(List<Track> album, int position) {
        PlayerPresenter presenter = PlayerPresenter.getInstance();
        presenter.setPlayList(album, position);
        startActivity(new Intent(getActivity(), PlayerActivity.class));
    }
}
