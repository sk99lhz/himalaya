package com.lhz.sk.himalaya;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.lhz.sk.himalaya.activitys.AboutActivity;
import com.lhz.sk.himalaya.activitys.PlayerActivity;
import com.lhz.sk.himalaya.activitys.SearchActivity;
import com.lhz.sk.himalaya.activitys.SettingActivity;
import com.lhz.sk.himalaya.adapters.IndicatorAdapter;
import com.lhz.sk.himalaya.adapters.ViewPageAdapter;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.interfaces.IBingCallBack;
import com.lhz.sk.himalaya.interfaces.IPlayerViewCallBack;
import com.lhz.sk.himalaya.presenters.BingPresenter;
import com.lhz.sk.himalaya.presenters.PlayerPresenter;
import com.lhz.sk.himalaya.presenters.RecommendPresenter;
import com.lhz.sk.himalaya.utils.AppTool;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.lhz.sk.himalaya.utils.ToastUtils;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

import static com.lhz.sk.himalaya.utils.Contants.BINGDATASP;
import static com.lhz.sk.himalaya.utils.Contants.BINGDATASPKEY;


public class MainActivity extends BaseActivity implements IPlayerViewCallBack, IBingCallBack {

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
    private NavigationView mNavigationView;
    private BingPresenter mBingPresenter;
    private ImageView mUserIv;
    private String mBingData = null;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppTool.RequestPermissions(MainActivity.this, Manifest.permission.INTERNET);
        AppTool.RequestPermissions(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
        mPreferences = getSharedPreferences(BINGDATASP, Context.MODE_PRIVATE);
        initView();
        mBingPresenter = BingPresenter.getInstance();
        mBingPresenter.registerViewCallback(this);
        initData();
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        initEvent();
        updatePlayState(mPlayerPresenter.isPlay());

    }

    private void initData() {
        if (!TextUtils.isEmpty(mPreferences.getString(BINGDATASPKEY, ""))) {
            mBingData = mPreferences.getString(BINGDATASPKEY, "");
            Glide.with(BaseApplication.getContext()).load(mBingData).into(mUserIv);
            Glide.with(BaseApplication.getContext()).load(mBingData).into(mTarckIv);
        } else {
            mBingPresenter.loadBingData();
        }

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
        mIndicatorAdapter.setOnIndicatorTabClickListener(index -> vp_main.setCurrentItem(index, false));
        mPlaymain.setOnClickListener(v -> {
            boolean playList = mPlayerPresenter.isPlayList();
            if (!playList) {
                playFirstRecommend();
            }
            startActivity(new Intent(MainActivity.this, PlayerActivity.class));
        });
        mPlayIv.setOnClickListener(v -> {
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

        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        return true;
                    case R.id.action_night:
                        ToastUtils.showToast(MainActivity.this, "功能完善中！");
                        break;
                    case R.id.action_timer:
                        ToastUtils.showToast(MainActivity.this, "测试中！");
                        break;

                    case R.id.action_changepasswordt:
                        ToastUtils.showToast(MainActivity.this, "功能完善中！");
                        return true;
                    case R.id.action_about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        return true;
                }
                return false;
            }
        });
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };
        int[] colors = new int[]{getResources().getColor(R.color.main_color),
                getResources().getColor(R.color.main_color)
        };
        ColorStateList csl = new ColorStateList(states, colors);
        mNavigationView.setItemTextColor(csl);
        mNavigationView.setItemIconTintList(csl);
        if (mBingData != null)
            Glide.with(BaseApplication.getContext()).load(mBingData).into(mUserIv);

    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.main_indicator);
        vp_main = findViewById(R.id.vp_main);
        mMagicIndicator.setBackgroundColor(getResources().getColor(R.color.main_color));
        mIndicatorAdapter = new IndicatorAdapter(this);
        mNavigationView = findViewById(R.id.navigation_view);
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
        mUserIv = mNavigationView.getHeaderView(0).findViewById(R.id.user_Iv);

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
        if (mBingPresenter != null) {
            mBingPresenter.unregisterViewCallback(this);
        }
        LogUtil.e(TAB, "onDestroy ===================");
    }

    private void updatePlayState(boolean isPlay) {
        if (mPlayIv != null) {
            mPlayIv.setImageResource(isPlay ? R.mipmap.play_black_press : R.mipmap.play_black_normal);

        }
    }

    public void onSearchClick(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void IBingData(String data) {
        this.mBingData = data;
        if (data != null) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(BINGDATASPKEY, data);
            editor.apply();
            Glide.with(BaseApplication.getContext()).load(data).into(mUserIv);
        }
    }

    @Override
    public void IBingFailed(Throwable throwable) {

    }
}