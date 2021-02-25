package com.lhz.sk.himalaya.bases;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.lhz.sk.himalaya.R;

/**
 * Created by song
 */
public class BaseActivity extends FragmentActivity {
    private ImageView mIvBack, mIvMe;
    private TextView mTvTitle;
    protected void initNavBar(boolean isShowBack, String title, boolean isShowMe) {
        mIvBack = findViewById(R.id.iv_back);
        mIvMe = findViewById(R.id.iv_me);
        mTvTitle = findViewById(R.id.tv_title);
        mIvBack.setVisibility(isShowBack ? View.VISIBLE : View.GONE);
        mIvMe.setVisibility(isShowMe ? View.VISIBLE : View.GONE);
        mTvTitle.setText(title);
        mIvBack.setOnClickListener(v -> onBackPressed());
    }

}
