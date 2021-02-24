package com.lhz.sk.himalaya;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lhz.sk.himalaya.adapters.DetailListAdapter;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.interfaces.IDetailViewCallBack;
import com.lhz.sk.himalaya.interfaces.IPlayerViewCallBack;
import com.lhz.sk.himalaya.interfaces.ISubscriptionViewCallBack;
import com.lhz.sk.himalaya.presenters.DetailPresenter;
import com.lhz.sk.himalaya.presenters.PlayerPresenter;
import com.lhz.sk.himalaya.presenters.SubscriptionPresenter;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.lhz.sk.himalaya.utils.ToastUtils;
import com.lhz.sk.himalaya.views.RoundRectImageView;
import com.lhz.sk.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class DetailsActivity extends BaseActivity implements IDetailViewCallBack, DetailListAdapter.onItemCallLister, IPlayerViewCallBack, ISubscriptionViewCallBack {

    private static final String TAB = "DetailsActivity";
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
    private ImageView mControlBtn;
    private TextView mControlTv;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTrack = null;

    private static int mCurrentIndex = 0;
    private TwinklingRefreshLayout mRefreslayout;
    private String mTitle;
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        mPresenter = DetailPresenter.getInstance();
        mPresenter.registerViewCallback(this);
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        updatePlayState(mPlayerPresenter.isPlay());
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscription();
        mSubscriptionPresenter.registerViewCallback(this);
        initListener();
        updateSubState();
    }

    private void updateSubState() {

        if (mSubscriptionPresenter != null) {
            boolean sub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(sub ? R.string.cancel_sub_text : R.string.sub_text);
        }
        mSubBtn.setOnClickListener(v -> {
            if (mSubscriptionPresenter != null) {
                boolean sub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                if (sub) {
                    mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                } else {
                    mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                }
            }
        });
    }


    private void initListener() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean playList = mPlayerPresenter.isPlayList();
                    if (playList) {
                        if (mPlayerPresenter.isPlay()) {

                            mPlayerPresenter.pause();
                        } else {

                            mPlayerPresenter.play();
                        }
                    } else {
                        mPlayerPresenter.setPlayList(mCurrentTrack, mCurrentIndex);
                    }

                }

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unregisterViewCallback(this);
        if (mPlayerPresenter != null)
            mPlayerPresenter.unregisterViewCallback(this);
        if (mSubscriptionPresenter != null)
            mSubscriptionPresenter.unregisterViewCallback(this);
    }

    private void initView() {
        mLargeCover = findViewById(R.id.iv_large_cover);
        mSmallCover = findViewById(R.id.viv_small_cover);
        mAlbumAuthor = findViewById(R.id.tv_album_author);
        mAlbumTitle = findViewById(R.id.tv_album_title);
        mMFlDetail = findViewById(R.id.detail_list_container);

        mControlBtn = findViewById(R.id.detail_play_control);
        mControlTv = findViewById(R.id.play_control_tv);
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
        mSubBtn = findViewById(R.id.detail_sub_btn);


    }

    private View createSuccessView(ViewGroup group) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.item_detail_list, group, false);
        mRvDetail = inflate.findViewById(R.id.album_detail_list);
        mRefreslayout = inflate.findViewById(R.id.refres_layout);
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
        //BezierLayout bezierLayout=new BezierLayout(this);
        //mRefreslayout.setHeaderView(bezierLayout);
        //mRefreslayout.setBottomHeight(140);
        mRefreslayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mRefreslayout.finishRefreshing();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mIsLoaderMore = true;
                if (mPresenter != null) {
                    mPresenter.loadMore();
                }
                mRefreslayout.finishLoadmore();
            }
        });
        return inflate;
    }

    private boolean mIsLoaderMore = false;

    @Override
    public void onDetailLoaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreslayout != null) {
            mRefreslayout.finishLoadmore();
            mIsLoaderMore = false;
        }
        this.mCurrentTrack = tracks;
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
        this.mCurrentAlbum = album;
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
    public void onLoadedFinshed(int size) {
        if (size > 0) {
            ToastUtils.showToast(DetailsActivity.this, "成功加载" + size + "条");
        } else {
            ToastUtils.showToast(DetailsActivity.this, "没有更多数据");
        }
    }

    @Override
    public void onRefreshFinshed(int size) {

    }

    @Override
    public void onItemClick(List<Track> album, int position) {
        PlayerPresenter presenter = PlayerPresenter.getInstance();
        presenter.setPlayList(album, position);
        startActivity(new Intent(this, PlayerActivity.class));
    }

    @Override
    public void onPlayStart() {
        if (mControlTv != null && mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_black_press);
            mControlTv.setText(mTitle);
        }

    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null && mControlTv != null) {
            mControlBtn.setImageResource(R.mipmap.play_black_normal);
            mControlTv.setText(R.string.click_play_tips_text);
        }

    }

    private void updatePlayState(boolean isPlay) {
        if (mControlTv != null && mControlBtn != null) {
            mControlBtn.setImageResource(isPlay ? R.mipmap.play_black_press : R.mipmap.play_black_normal);
            if (!isPlay)
                mControlTv.setText(R.string.click_play_tips_text);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null && mControlTv != null) {
            mControlBtn.setImageResource(R.mipmap.play_black_normal);
            mControlTv.setText(R.string.click_play_tips_text);
        }
    }

    @Override
    public void NextPlay(Track track) {

    }

    @Override
    public void onPrePlay() {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onProgressChange(int current, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpData(Track track, int playIndex) {
        if (track != null) {
            this.mTitle = track.getTrackTitle();
            if (mControlTv != null && !TextUtils.isEmpty(mTitle)) {
                mControlTv.setText(mTitle);
            }
        }

    }

    @Override
    public void updateListOrder(boolean isOrder) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (mSubBtn != null) {
            if (isSuccess) {
                mSubBtn.setText(R.string.cancel_sub_text);
            }
        }
        String string = isSuccess ? "订阅成功" : "订阅失败";
        ToastUtils.showToast(DetailsActivity.this, string);

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (mSubBtn != null) {
            if (isSuccess) {
                mSubBtn.setText(R.string.sub_text);
            }
        }
        String string = isSuccess ? "删除成功" : "删除失败";
        ToastUtils.showToast(DetailsActivity.this, string);
    }

    @Override
    public void onSubscritpionsLoad(List<Album> albums) {
        for (Album album : albums) {
            LogUtil.e(TAB, "albums=====" + album.getAlbumTitle());
        }
    }

    @Override
    public void onSubToMany() {
        ToastUtils.showToast(this,"订阅不超过一百！");
    }
}