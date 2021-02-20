package com.lhz.sk.himalaya.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lhz.sk.himalaya.R;

/**
 * Created by song
 */
public class LoadingView extends androidx.appcompat.widget.AppCompatImageView {
    private int rotate = 0;
    private boolean isRotate = false;

    public LoadingView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading_new);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(rotate, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isRotate = true;
        post(new Runnable() {
            @Override
            public void run() {
                rotate += 30;
                rotate = rotate <= 360 ? rotate : 0;
                  invalidate();
                if (isRotate)
                    postDelayed(this, 100);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRotate = false;
    }
}
