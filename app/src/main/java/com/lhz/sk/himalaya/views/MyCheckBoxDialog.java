package com.lhz.sk.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lhz.sk.himalaya.R;

/**
 * Created by song
 */
public class MyCheckBoxDialog extends Dialog {

    private TextView mCanceSub;
    private TextView mGiveUp;
    private CheckBox mCheckBox;

    public MyCheckBoxDialog(@NonNull Context context) {
        this(context, 0);
    }

    public MyCheckBoxDialog(@NonNull Context context, int themeResId) {
        this(context, true, null);
    }

    protected MyCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialong_checkbox_my);
        intiView();
        mCanceSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogActionCilckListener != null)
                    mDialogActionCilckListener.onCancel();
                dismiss();
            }
        });
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogActionCilckListener != null) {
                    boolean checked = mCheckBox.isChecked();
                    mDialogActionCilckListener.onGiveUp(checked);
                    dismiss();
                }

            }
        });
    }

    private void intiView() {
        mCanceSub = findViewById(R.id.cancel_tv);
        mGiveUp = findViewById(R.id.give_tv);
        mCheckBox = findViewById(R.id.dialog_checkbox);
    }

    OnDialogActionCilckListener mDialogActionCilckListener;

    public void setOnDialogActionCilckListener(OnDialogActionCilckListener mDialogActionCilckListener) {
        this.mDialogActionCilckListener = mDialogActionCilckListener;
    }

    public interface OnDialogActionCilckListener {
        void onCancel();

        void onGiveUp(boolean isCheck);
    }
}
