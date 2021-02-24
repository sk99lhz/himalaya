package com.lhz.sk.himalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.request.RequestOptions;
import com.lhz.sk.himalaya.adapters.PlayerPageAdapter;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.interfaces.IPlayerViewCallBack;
import com.lhz.sk.himalaya.presenters.PlayerPresenter;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.lhz.sk.himalaya.utils.ToastUtils;
import com.lhz.sk.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerViewCallBack {

    private static final String TAB = "PlayerActivity";
    private PlayerPresenter mPlayerPresenter;
    private ImageView mControlBtn;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mSeekBar;
    private int mCurrent = 0;
    private boolean mTouch = false;
    private ImageView mPlayNex;
    private ImageView mPlayPre;
    private TextView mTracktitle;
    private String mTitle;
    private ViewPager mPager;
    private PlayerPageAdapter mPlayerPageAdapter;

    private boolean isUserSlidePage = false;
    private ImageView mIvbg;
    private String mUrlLarge;
    private ImageView mIvPlayModeSwich;
    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeMap = new HashMap<>();

    static {
        sPlayModeMap.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeMap.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeMap.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeMap.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);

    }

    private ImageView mPlayList;
    private SobPopWindow mPopWindow;
    private ValueAnimator mValueAnimator;
    private ValueAnimator mOutValueAnimator;
    private static int ANIMATOR_DURATION = 400;
    private int mCurrentplayIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
       // mPlayerPresenter.getPlayList();
        iniEvent();
        initBgAnimation();
        updatePlayState(mPlayerPresenter.isPlay());
    }


    private void initBgAnimation() {
        mValueAnimator = ValueAnimator.ofFloat(1.0f, 0.6f);

        mValueAnimator.setDuration(ANIMATOR_DURATION);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateBgAlpha(animation.getAnimatedFraction());
            }
        });
        mOutValueAnimator = ValueAnimator.ofFloat(1.0f, 0.6f);
        mOutValueAnimator.setDuration(ANIMATOR_DURATION);
        mOutValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateBgAlpha(animation.getAnimatedFraction());
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void iniEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlay()) {

                    mPlayerPresenter.pause();
                } else {

                    mPlayerPresenter.play();
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mTouch)
                    mCurrent = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTouch = false;
                mPlayerPresenter.seekTo(mCurrent);
            }
        });
        mPlayNex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();

                }
            }
        });
        mPlayPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();

                }
            }
        });
        if (mTitle != null) {
            mTracktitle.setText(mTitle);
        }
        if (mUrlLarge != null)
            Glide.with(this)
                    .load(mUrlLarge)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 15)))
                    .into(mIvbg);


        mPager.setAdapter(mPlayerPageAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (mPlayerPresenter != null && isUserSlidePage) {
                    mPlayerPresenter.playByIndex(position);
                }
                isUserSlidePage = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPager.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isUserSlidePage = true;
                    break;

            }

            return false;
        });

        mIvPlayModeSwich.setOnClickListener(v -> {
            switchPlayMode();

        });

        mPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                mValueAnimator.start();
            }
        });

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mOutValueAnimator.start();
            }
        });
        mPopWindow.setonSobPopWindowItemListener(new SobPopWindow.onSobPopWindowItemListener() {
            @Override
            public void onItemClick(int index) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(index);
                }

            }
        });
        mPopWindow.setonPlayListModelClickListener(new SobPopWindow.onPlayListModelClickListener() {
            @Override
            public void onPlayModeClick() {
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.revesePlayList();
                }
                mPopWindow.updateOrderIcon(!testOrder);
                testOrder = !testOrder;
            }
        });

    }

    private void switchPlayMode() {
        XmPlayListControl.PlayMode playMode = sPlayModeMap.get(mCurrentMode);
        if (mPlayerPresenter != null) {
            mPlayerPresenter.swicthPlayMode(playMode);


        }
    }

    public void updateBgAlpha(float alpha) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    private void updatePlayModeView() {
        int resId = R.drawable.ic_baseline_repeat_24;
        switch (mCurrentMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.ic_baseline_reorder_24;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.ic_baseline_repeat_24;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.ic_baseline_swap_calls_24;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.ic_baseline_repeat_one_24;
                break;
        }

        mIvPlayModeSwich.setImageResource(resId);

    }

    private void initView() {
        mControlBtn = findViewById(R.id.ic_bg_play);
        mTotalDuration = findViewById(R.id.track_duration_i);
        mCurrentPosition = findViewById(R.id.track_duration_p);
        mSeekBar = findViewById(R.id.sb_progress);
        mPlayNex = findViewById(R.id.ic_skip_next);
        mPlayPre = findViewById(R.id.ic_skip_previous);
        mTracktitle = findViewById(R.id.track_title);
        mPager = findViewById(R.id.play_vp);
        mIvbg = findViewById(R.id.iv_bg);
        mIvPlayModeSwich = findViewById(R.id.ic_repeat);
        mPlayList = findViewById(R.id.ic_music_lists);
        mPopWindow = new SobPopWindow();
        mPlayerPageAdapter = new PlayerPageAdapter(this);
    }

    @Override
    public void onPlayStart() {
        if (mControlBtn != null)
            mControlBtn.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);

    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null)
            mControlBtn.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
    }
    private void updatePlayState(boolean isPlay) {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(isPlay ? R.drawable.ic_baseline_pause_circle_filled_24 : R.drawable.ic_baseline_play_circle_filled_24);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null)
            mControlBtn.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);

    }

    @Override
    public void NextPlay(Track track) {

    }

    @Override
    public void onPrePlay() {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        if (list != null) {
            if (mPlayerPageAdapter != null) {
                mPlayerPageAdapter.setData(list);
            }
            if (mPopWindow != null) {
                mPopWindow.setListData(list);
            }

        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {
        this.mCurrentMode = mode;
        mPopWindow.updatePlayModeView(mCurrentMode);
        updatePlayModeView();
    }

    public void onProgressChange(int current, int total) {
        LogUtil.e(TAB,"current  "+current+"   total   "+  total);
        mSeekBar.setMax(total);
        String totalTime;
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            totalTime = mHourFormat.format(total);
            currentPosition = mHourFormat.format(current);
        } else {
            totalTime = mMinFormat.format(total);
            currentPosition = mMinFormat.format(current);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalTime);
        }
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }

        if (!mTouch) {
            mSeekBar.setProgress(current);
        }

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpData(Track track, int playIndex) {
        this.mCurrentplayIndex=playIndex;
        if (track == null) {
           // ToastUtils.showToast(PlayerActivity.this,"没有播放");
            return;
        }
        this.mTitle = track.getTrackTitle();
        mUrlLarge = track.getCoverUrlLarge();
        if (mTracktitle != null) {
            mTracktitle.setText(track.getTrackTitle());
        }
        if (mPager != null) {
           mPager.setCurrentItem(playIndex, true);
        }

        if (mPopWindow != null) {
            mPopWindow.setCurrentPlay(playIndex);
        }
        if (mIvbg != null) {
            Glide.with(this)
                    .load(track.getCoverUrlLarge())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 15)))

                    .into(mIvbg);
        }

    }

    @Override
    public void updateListOrder(boolean isOrder) {
        mPopWindow.updateOrderIcon(isOrder);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerPresenter.unregisterViewCallback(this);
    }

    private boolean testOrder = false;
}