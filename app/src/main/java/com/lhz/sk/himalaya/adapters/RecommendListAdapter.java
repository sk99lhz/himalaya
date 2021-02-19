package com.lhz.sk.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhz.sk.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.ViewHolder> {

    private List<Album> mData = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_re_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {

            ImageView albumCover = itemView.findViewById(R.id.album_cover);
            TextView albumTitle = itemView.findViewById(R.id.album_title);
            TextView albumDesc = itemView.findViewById(R.id.album_desc);
            TextView albumCountSize = itemView.findViewById(R.id.album_count_size);
            TextView albumPlayCount = itemView.findViewById(R.id.album_play_count);

            albumTitle.setText(album.getAlbumTitle());
            albumDesc.setText(album.getAlbumIntro());

            albumCountSize.setText(album.getPlayCount() + "");
            albumPlayCount.setText(album.getIncludeTrackCount() + "");
            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCover);
        }
    }
}
