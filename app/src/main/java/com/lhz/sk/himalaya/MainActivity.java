package com.lhz.sk.himalaya;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lhz.sk.himalaya.adapters.IndicatorAdapter;
import com.lhz.sk.himalaya.adapters.PlayerPageAdapter;
import com.lhz.sk.himalaya.adapters.ViewPageAdapter;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.interfaces.IPlayerViewCallBack;
import com.lhz.sk.himalaya.presenters.PlayerPresenter;
import com.lhz.sk.himalaya.presenters.RecommendPresenter;
import com.lhz.sk.himalaya.utils.AppTool;
import com.lhz.sk.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends BaseActivity implements IPlayerViewCallBack {

    private static final String TAB = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager vp_main;
    private IndicatorAdapter mIndicatorAdapter;
    private ImageView mTarckIv;
    private TextView mMiantitle;
    private TextView mMiansiger;
    private ImageView mPlayIv;
    private PlayerPresenter mPlayerPresenter;
    private String mTrackTitle;
    private String mNickname;
    private String mCoverUrlMiddle;
    private long mAlbumId;
    private LinearLayout mPlaymain;
    private Track mCurrenttrack;
    private int mCurrentplayIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppTool.RequestPermissions(MainActivity.this, Manifest.permission.INTERNET);
        AppTool.RequestPermissions(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
        initView();
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        initEvent();
        updatePlayState(mPlayerPresenter.isPlay());
        mPlayIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean playList = mPlayerPresenter.isPlayList();
                    if (!playList) {
                        playFirstRecommend();
                    } else {
                        if (mPlayerPresenter.isPlay()) {

                            mPlayerPresenter.pause();
                        } else {

                            mPlayerPresenter.play();
                        }
                    }

                }

            }
        });
    }

    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null) {
            Album album = currentRecommend.get(6);
            mAlbumId = album.getId();
            mPlayerPresenter.playByAlbumId(mAlbumId);
        }
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTabClickListener(index -> vp_main.setCurrentItem(index,false));
        mPlaymain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean playList = mPlayerPresenter.isPlayList();
                if (!playList) {
                    playFirstRecommend();
                }
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
            }
        });

    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.main_indicator);
        vp_main = findViewById(R.id.vp_main);
        mMagicIndicator.setBackgroundColor(getResources().getColor(R.color.main_color));
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(mIndicatorAdapter);
        commonNavigator.setAdjustMode(true);
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        vp_main.setAdapter(viewPageAdapter);

        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, vp_main);

        mTarckIv = findViewById(R.id.tarck_covre);
        mMiantitle = findViewById(R.id.main_titile);
        mMiansiger = findViewById(R.id.main_siger);
        mPlayIv = findViewById(R.id.play_iv);
        mPlaymain = findViewById(R.id.play_main);


    }

    @Override
    public void onPlayStart() {
        if (mPlayIv != null) {
            mPlayIv.setImageResource(R.mipmap.play_black_press);
        }
    }

    @Override
    public void onPlayPause() {
        if (mPlayIv != null) {
            mPlayIv.setImageResource(R.mipmap.play_black_normal);
        }
    }

    @Override
    public void onPlayStop() {
        if (mPlayIv != null) {
            mPlayIv.setImageResource(R.mipmap.play_black_normal);
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
            this.mCurrenttrack = track;
            this.mCurrentplayIndex = playIndex;
            mTrackTitle = track.getTrackTitle();
            mNickname = track.getAnnouncer().getNickname();
            mCoverUrlMiddle = track.getCoverUrlMiddle();
            if (mMiantitle != null)
                mMiantitle.setText(mTrackTitle);
            if (mMiansiger != null)
                mMiansiger.setText(mNickname);
            if (mTarckIv != null)
                Glide.with(this).load(mCoverUrlMiddle).into(mTarckIv);
        }
    }

    @Override
    public void updateListOrder(boolean isOrder) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
        }
    }

    private void updatePlayState(boolean isPlay) {
        if (mPlayIv != null) {
            mPlayIv.setImageResource(isPlay ? R.mipmap.play_black_press : R.mipmap.play_black_normal);

        }
    }

    public void onSearchClick(View view) {
        Intent intent=new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}