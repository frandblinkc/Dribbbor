package com.tangyang.fribbble.view.bucket_list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Bucket;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.view.base.LoadMoreListener;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by tangy on 9/16/2016.
 */
public class BucketListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_SHOT = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private LoadMoreListener loadMoreListener;
    private boolean showLoading;
    private List<Bucket> data;

    public BucketListAdapter(List<Bucket> data, LoadMoreListener loadMoreListener) {
        this.data = data;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SHOT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_bucket, parent, false);
            return new BucketViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_loading, parent, false);
            return new RecyclerView.ViewHolder(view) {}; //create an anonymous subclass and instantiate it
        }
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // fire onLoadMore() at the beginning when starting to display last item
        if (data.size() == 0 || position == data.size() - 1) {
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            return;
        }

        Bucket bucket = data.get(position);
        BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;
        // 0 -> 0 shot
        // 1 -> 1 shot
        // 2 -> 2 shots
        String bucketShotCountString = MessageFormat.format(
                holder.itemView.getContext().getResources().getString(R.string.shot_count),
                bucket.shots_count);

        bucketViewHolder.bucketName.setText(bucket.name);
        bucketViewHolder.bucketShotCount.setText(bucketShotCountString);
    }


    @Override
    public int getItemCount() {
        return showLoading? data.size() + 1: data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < data.size()? VIEW_TYPE_SHOT: VIEW_TYPE_LOADING;
    }

    public void append(@NonNull List<Bucket> moreBuckets) {
        data.addAll(moreBuckets);
        notifyDataSetChanged();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public int getDataCount() {
        return data.size();
    }
}
