package com.lhz.sk.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.tech.NfcV;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.adapters.PlayListAdapter;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * Created by song
 */
public class SobPopWindow extends PopupWindow {

    private final View mView;
    private View mCloseTv;
    private RecyclerView mRvPlayList;
    private PlayListAdapter mPlayListAdapter;
    private onSobPopWindowItemListener mOnSobPopWindowItemListener;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mMPlayModeLy;
    private onPlayListModelClickListener listener = null;
    private View mContainer;
    private TextView mOrderTv;
    private ImageView mOrderIv;


    public SobPopWindow() {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mView = LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.pop_play_list, null);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setContentView(mView);
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initEvent() {
        mCloseTv.setOnClickListener(v -> SobPopWindow.this.dismiss());
        mMPlayModeLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPlayModeClick();
                }
            }
        });
        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onOrderClick();
            }
        });

    }

    public void updateOrderIcon(boolean isOrder) {
        mOrderIv.setImageResource(isOrder ?  R.mipmap.play_onnext:R.mipmap.play_next);
        mOrderTv.setText(isOrder ? "倒序" : "顺序");
    }

    private void initView() {
        mCloseTv = mView.findViewById(R.id.play_list_close);
        mRvPlayList = mView.findViewById(R.id.play_control_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getContext());
        mRvPlayList.setLayoutManager(layoutManager);
        mPlayListAdapter = new PlayListAdapter();
        mRvPlayList.setAdapter(mPlayListAdapter);
        mPlayModeTv = mView.findViewById(R.id.play_control_tv);
        mPlayModeIv = mView.findViewById(R.id.play_control_iv);
        mMPlayModeLy = mView.findViewById(R.id.layout);
        mContainer = mView.findViewById(R.id.play_mode_order);
        mOrderTv = mView.findViewById(R.id.order_text);
        mOrderIv = mView.findViewById(R.id.paly_list_order_iv);


    }

    public void setListData(List<Track> data) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setListData(data);
        }
    }

    public void setCurrentPlay(int playIndex) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlay(playIndex);
            mRvPlayList.scrollToPosition(playIndex);
        }
    }


    public void setonSobPopWindowItemListener(onSobPopWindowItemListener listener) {
        mPlayListAdapter.setOnItemListener(listener);
    }

    public void setonPlayListModelClickListener(onPlayListModelClickListener listener) {
        this.listener = listener;
    }

    public void updatePlayModeView(XmPlayListControl.PlayMode currentMode) {
        updatePlayMode(currentMode);
    }

    private void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        int resId = R.drawable.ic_baseline_repeat_24_list;
        int textId = R.string.play_mode_order_text;
        switch (currentMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.ic_baseline_reorder_24_lsit;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.ic_baseline_repeat_one_24_list;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.ic_baseline_swap_calls_24_list;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.ic_baseline_repeat_one_24_list;
                textId = R.string.play_mode_single_text;
                break;
        }

        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }

    public interface onSobPopWindowItemListener {
        void onItemClick(int index);
    }

    public interface onPlayListModelClickListener {
        void onPlayModeClick();

        void onOrderClick();
    }


}
