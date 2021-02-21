package com.lhz.sk.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.lhz.sk.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    private List<Track> mPlayListData = new ArrayList<>();
    private int mPlayIndex;
    private SobPopWindow.onSobPopWindowItemListener mItemListener = null;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView mTitle = holder.itemView.findViewById(R.id.paly_title);
        Track track = mPlayListData.get(position);
        mTitle.setText(track.getTrackTitle());
        View playIv = holder.itemView.findViewById(R.id.paly_list_iv);
        mTitle.setTextColor((BaseApplication.getContext().getResources().getColor(mPlayIndex == position ? R.color.main_color : R.color.main_color_two)));
        playIv.setVisibility(mPlayIndex == position ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemListener != null)
                    mItemListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayListData.size();
    }

    public void setListData(List<Track> data) {
        mPlayListData.clear();
        mPlayListData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlay(int playIndex) {
        this.mPlayIndex = playIndex;
        notifyDataSetChanged();
    }

    public void setOnItemListener(SobPopWindow.onSobPopWindowItemListener listener) {
        this.mItemListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
