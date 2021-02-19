package com.lhz.sk.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.lhz.sk.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * Created by song
 */
public class IndicatorAdapter extends CommonNavigatorAdapter {
    private String[] mTitles;
    private OnIndicatorTabClickListener onIndicatorTabClickListener;

    public void setOnIndicatorTabClickListener(OnIndicatorTabClickListener onIndicatorTabClickListener) {
        this.onIndicatorTabClickListener = onIndicatorTabClickListener;
    }

    public IndicatorAdapter(Context context) {
        mTitles = context.getResources().getStringArray(R.array.indicator_title);
    }

    @Override
    public int getCount() {
        if (mTitles != null)
            return mTitles.length;
        else
            return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        simplePagerTitleView.setTextSize(18);
        simplePagerTitleView.setText(mTitles[index]);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onIndicatorTabClickListener!=null)
                    onIndicatorTabClickListener.onTabClick(index);
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }

    public interface OnIndicatorTabClickListener {
        void onTabClick(int index);
    }
}
