package com.lhz.sk.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;


/**
 * Created by song
 */
public class RollTextView extends androidx.appcompat.widget.AppCompatTextView {
    public RollTextView (Context context) {
        super(context);
    }
    public RollTextView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public RollTextView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
