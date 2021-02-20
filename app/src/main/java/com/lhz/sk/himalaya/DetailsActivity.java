package com.lhz.sk.himalaya;


import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
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
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class DetailsActivity extends BaseActivity implements IDetailViewCallBack {

    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private DetailPresenter mPresenter;
    private int mCurrent = 1;
    private RecyclerView mRvDetail;
    private DetailListAdapter mDetailListAdapter;


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
        mRvDetail = findViewById(R.id.detail_list_container);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvDetail.setLayoutManager(layoutManager);
        mDetailListAdapter = new DetailListAdapter();
        mRvDetail.setAdapter(mDetailListAdapter);
        mRvDetail.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
              outRect.top= UIUtil.dip2px(DetailsActivity.this,2);
                outRect.bottom= UIUtil.dip2px(DetailsActivity.this,2);
                outRect.right= UIUtil.dip2px(DetailsActivity.this,5);
                outRect.left= UIUtil.dip2px(DetailsActivity.this,5);
            }
        });

    }

    @Override
    public void onDetailLoaded(List<Track> tracks) {
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        mPresenter.getAlbumDetail((int) album.getId(), mCurrent);
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
}