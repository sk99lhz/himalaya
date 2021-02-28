package com.lhz.sk.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lhz.sk.himalaya.activitys.DetailsActivity;
import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.adapters.AlbumListAdapter;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.bases.BaseFragment;
import com.lhz.sk.himalaya.interfaces.ISubscriptionPresenter;
import com.lhz.sk.himalaya.interfaces.ISubscriptionViewCallBack;
import com.lhz.sk.himalaya.presenters.DetailPresenter;
import com.lhz.sk.himalaya.presenters.SubscriptionPresenter;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.lhz.sk.himalaya.utils.ToastUtils;
import com.lhz.sk.himalaya.views.MyDialog;
import com.lhz.sk.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by song
 */
public class SubscriptionFragment extends BaseFragment implements ISubscriptionViewCallBack, AlbumListAdapter.onAlbumLongItemCallLister, AlbumListAdapter.onAlbumItemCallLister {

    private static final String TAB = "SubscriptionFragment";
    private ISubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mRecyclerView;
    private TwinklingRefreshLayout mTwinklingRefreshLayout;
    private AlbumListAdapter mAlbumListAdapter;
    private Album mCurrentClickAlbum = null;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        FrameLayout inflate = (FrameLayout) inflater.inflate(R.layout.fragment_sn, container, false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup group) {
                    return createSuccessView(inflater);
                }
                @Override
                protected View getEmptyView() {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView textView = view.findViewById(R.id.tv_enty);
                    textView.setText("没有订阅记录！");
                    textView.setTextColor(getResources().getColor(R.color.main_color));
                    return view;
                }
            };

        }

        if (mUiLoader.getParent() instanceof ViewGroup)
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        inflate.addView(mUiLoader);
        return inflate;
    }

    private View createSuccessView(LayoutInflater inflater) {
        View inflate = LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.item_sub_fm, null);
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscription();
        mRecyclerView = inflate.findViewById(R.id.rd_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
        mAlbumListAdapter = new AlbumListAdapter();
        mRecyclerView.setAdapter(mAlbumListAdapter);
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
        mAlbumListAdapter.setonAlbumItemCallLister(this);
        mAlbumListAdapter.setonAlbumLongItemCallLister(this);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return inflate;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        int s = isSuccess ? R.string.cancel_sub_success : R.string.cancel_sub_fai;
        ToastUtils.showToast(BaseApplication.getContext(), s);
    }

    @Override
    public void onSubscritpionsLoad(List<Album> albums) {
        if (albums.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }

        if (mAlbumListAdapter != null) {
            if (albums != null) {
                mAlbumListAdapter.setData(albums);
            }
        }
    }

    @Override
    public void onSubToMany() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSubscriptionPresenter.unregisterViewCallback(this);
        mAlbumListAdapter.setonAlbumItemCallLister(this);
        mAlbumListAdapter.setonAlbumLongItemCallLister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLongItemClick(Album album) {
        this.mCurrentClickAlbum = album;
        MyDialog myDialog = new MyDialog(getActivity());
        myDialog.setOnDialogActionCilckListener(new MyDialog.OnDialogActionCilckListener() {
            @Override
            public void onCancel() {
                if (mCurrentClickAlbum != null && mSubscriptionPresenter != null) {
                    mSubscriptionPresenter.deleteSubscription(mCurrentClickAlbum);
                }
            }

            @Override
            public void onGiveUp() {

            }
        });
        myDialog.show();
    }

    @Override
    public void onItemClick(int position, Album album) {
        DetailPresenter presenter = DetailPresenter.getInstance();
        presenter.setTargetAlbum(album);
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        startActivity(intent);
    }
}
