package com.lhz.sk.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhz.sk.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by song
 */
public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.ViewHolder> {
    private List<Track> mDetailData = new ArrayList<>();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View itemView = holder.itemView;
        TextView ordetTv = itemView.findViewById(R.id.order_text);
        TextView tiltleTv = itemView.findViewById(R.id.detail_item_title);
        TextView playCountTv = itemView.findViewById(R.id.detail_item_play_count);
        TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
        TextView updateDateTv = itemView.findViewById(R.id.detail_item_update_time);

        Track track = mDetailData.get(position);
        ordetTv.setText(track.getOrderNum()+1+"");
        tiltleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount()+"");
        int Duration = track.getDuration() * 1000;
        String format = mDurationFormat.format(Duration);
        durationTv.setText(format);
        updateDateTv.setText(mSimpleDateFormat.format(track.getUpdatedAt()));

    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        if (tracks != null) {
            mDetailData.clear();
            mDetailData.addAll(tracks);
            notifyDataSetChanged();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
