package com.lhz.sk.himalaya;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lhz.sk.himalaya.adapters.DetailListAdapter;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.interfaces.IDetailViewCallBack;
import com.lhz.sk.himalaya.presenters.DetailPresenter;
import com.lhz.sk.himalaya.presenters.RecommendPresenter;
import com.lhz.sk.himalaya.views.RoundRectImageView;
import com.lhz.sk.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class DetailsActivity extends BaseActivity implements IDetailViewCallBack, DetailListAdapter.onDetailItemCallLister {

    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private DetailPresenter mPresenter;
    private int mCurrent = 1;
    private RecyclerView mRvDetail;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mMFlDetail;
    private UILoader mUiLoader;
    private long mAlbumId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        mPresenter = DetailPresenter.getInstance();
        mPresenter.registerViewCallback(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unregisterViewCallback(this);
    }

    private void initView() {
        mLargeCover = findViewById(R.id.iv_large_cover);
        mSmallCover = findViewById(R.id.viv_small_cover);
        mAlbumAuthor = findViewById(R.id.tv_album_author);
        mAlbumTitle = findViewById(R.id.tv_album_title);
        mMFlDetail = findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup group) {
                    return createSuccessView(group);
                }
            };
        }
        mUiLoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void OnRetry() {
                if (mPresenter != null) {
                    mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
                    mPresenter.getAlbumDetail((int) mAlbumId, mCurrent);
                }
            }
        });

        mMFlDetail.removeAllViews();
        mMFlDetail.addView(mUiLoader);
        mDetailListAdapter.setItemCallLister(this);

    }

    private View createSuccessView(ViewGroup group) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.item_detail_list, group, false);
        mRvDetail = inflate.findViewById(R.id.album_detail_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvDetail.setLayoutManager(layoutManager);
        mDetailListAdapter = new DetailListAdapter();
        mRvDetail.setAdapter(mDetailListAdapter);
        mRvDetail.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(DetailsActivity.this, 2);
                outRect.bottom = UIUtil.dip2px(DetailsActivity.this, 2);
                outRect.right = UIUtil.dip2px(DetailsActivity.this, 5);
                outRect.left = UIUtil.dip2px(DetailsActivity.this, 5);
            }
        });

        return inflate;
    }

    @Override
    public void onDetailLoaded(List<Track> tracks) {
        if (tracks == null || tracks.size() == 0) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        mAlbumId = album.getId();
        mPresenter.getAlbumDetail((int) mAlbumId, mCurrent);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (mAlbumTitle != null)
            mAlbumTitle.setText(album.getAlbumTitle());
        if (mAlbumAuthor != null)
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        //TODO
        if (mLargeCover != null) {
            Glide.with(this)
                    .load(album.getCoverUrlLarge())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 15)))

                    .into(mLargeCover);
        }

        if (mSmallCover != null)
            Glide.with(this).load(album.getCoverUrlLarge()).into(mSmallCover);
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        startActivity(new Intent(this, PlayerActivity.class));
    }
}