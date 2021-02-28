package com.lhz.sk.himalaya.activitys;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lhz.sk.himalaya.MainActivity;
import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.interfaces.IBingCallBack;
import com.lhz.sk.himalaya.interfaces.IBingPresenters;
import com.lhz.sk.himalaya.presenters.BingPresenter;

import static com.lhz.sk.himalaya.utils.Contants.BINGDATASP;
import static com.lhz.sk.himalaya.utils.Contants.BINGDATASPKEY;

public class WelcomeActivity extends BaseActivity implements IBingCallBack {
    private TranslateAnimation translateAnimation;
    private SharedPreferences mPreferences;
    private IBingPresenters mBingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
    }

    private void initView() {
        mBingPresenter = BingPresenter.getInstance();
        mBingPresenter.registerViewCallback(this);
        mBingPresenter.loadBingData();
        TextView tv = findViewById(R.id.tv_translate);
        final boolean isLong = true;
        tv.post(() -> {
            translateAnimation = new TranslateAnimation(0, tv.getWidth(), 0, 0);
            translateAnimation.setDuration(3000);
            translateAnimation.setFillAfter(true);
            tv.startAnimation(translateAnimation);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isLong) {
                        toMain();
                    } else {
                        toLogin();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        });
    }

    private void toLogin() {
       /* Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();*/
    }

    private void toMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void IBingData(String data) {
        if (data != null) {
            mPreferences = getSharedPreferences(BINGDATASP, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(BINGDATASPKEY, data);
            editor.apply();
        }
    }

    @Override
    public void IBingFailed(Throwable throwable) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBingPresenter.unregisterViewCallback(this);
    }
}