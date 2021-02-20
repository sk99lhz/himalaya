package com.lhz.sk.himalaya;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.os.Bundle;

import com.lhz.sk.himalaya.adapters.IndicatorAdapter;
import com.lhz.sk.himalaya.adapters.ViewPageAdapter;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.utils.AppTool;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends BaseActivity {

    private static final String TAB = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager vp_main;
    private IndicatorAdapter mIndicatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppTool.RequestPermissions(MainActivity.this, Manifest.permission.INTERNET);
        AppTool.RequestPermissions(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);
        initView();
        initEvent();
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTabClickListener(index -> vp_main.setCurrentItem(index));
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
    }
}