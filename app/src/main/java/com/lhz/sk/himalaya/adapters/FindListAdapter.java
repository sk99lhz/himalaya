package com.lhz.sk.himalaya.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lhz.sk.himalaya.R;
import com.lhz.sk.himalaya.bases.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class FindListAdapter extends RecyclerView.Adapter<FindListAdapter.ViewHolder> {
    private List<Announcer> mAnnouncers = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_find, parent, false);
        return new ViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView IvIcon = holder.itemView.findViewById(R.id.iv_icon);
        TextView TvName = holder.itemView.findViewById(R.id.tv_name);
        TvName.setText(mAnnouncers.get(position).getNickname());
        mAnnouncers.get(position).getAnnouncerId();
        Glide.with(BaseApplication.getContext()).load(mAnnouncers.get(position).getAvatarUrl()).into(IvIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFindListItemCallLister != null)
                    mOnFindListItemCallLister.onItemClick(position, mAnnouncers.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAnnouncers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setData(List<Announcer> announcers) {
        mAnnouncers.addAll(announcers);
        notifyDataSetChanged();
    }

    private onFindListItemCallLister mOnFindListItemCallLister;

    public void setonFindListItemCallLister(onFindListItemCallLister callLister) {
        mOnFindListItemCallLister = callLister;
    }

    public interface onFindListItemCallLister {
        void onItemClick(int position, Announcer announcer);
    }
}
