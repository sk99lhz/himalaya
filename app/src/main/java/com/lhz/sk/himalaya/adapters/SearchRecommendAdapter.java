package com.lhz.sk.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhz.sk.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by song
 */
public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.ViewHolder> {
    private List<QueryResult> results = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_re, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView mSearchTv = holder.itemView.findViewById(R.id.item_search_tv);
        TextView mSearchIndex = holder.itemView.findViewById(R.id.index_tv);
        mSearchTv.setText(results.get(position).getKeyword());
        mSearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSearchItemListener != null)
                    mOnSearchItemListener.onSearchItem(results.get(position).getKeyword());
            }
        });
        mSearchIndex.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void setData(List<QueryResult> results) {
        this.results.clear();
        this.results.addAll(results);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    onSearchItemListener mOnSearchItemListener = null;

    public void setOnSearchItemListener(onSearchItemListener listener) {
        this.mOnSearchItemListener = listener;
    }

    public interface onSearchItemListener {
        void onSearchItem(String data);
    }
}
