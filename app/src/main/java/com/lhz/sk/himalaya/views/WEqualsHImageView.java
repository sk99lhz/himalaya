package com.lhz.sk.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * Created by song
 */
public class WEqualsHImageView extends androidx.appcompat.widget.AppCompatImageView {


    public WEqualsHImageView(Context context) {
        super(context);
    }

    public WEqualsHImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WEqualsHImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
