package com.lhz.sk.himalaya.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.lhz.sk.himalaya.PlayerActivity;
import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.presenters.PlayerPresenter;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class PlayerPageAdapter extends PagerAdapter {
    private List<Track> mTrack = new ArrayList<>();
    private ImageView mImageView;
    private Animation mAnimation;
    private Context mContext;
    public static View mView;

    public PlayerPageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mTrack.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mContext = container.getContext();
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_tarck_pager, container, false);
        container.addView(inflate);
        mImageView = inflate.findViewById(R.id.tarck_page_item);
        Track track = mTrack.get(position);
        String urlLarge = track.getCoverUrlLarge();
        Glide.with(container.getContext()).load(urlLarge).into(mImageView);
        return inflate;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        this.mView= (View) object;
        super.setPrimaryItem(container, position, object);
    }

    public void setData(List<Track> tracks) {
        mTrack.clear();
        mTrack.addAll(tracks);
        notifyDataSetChanged();
    }



}
