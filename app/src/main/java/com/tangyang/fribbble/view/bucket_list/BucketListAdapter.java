package com.tangyang.fribbble.view.bucket_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Bucket;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by tangy on 9/16/2016.
 */
public class BucketListAdapter extends RecyclerView.Adapter {
    private List<Bucket> data;

    public BucketListAdapter(List<Bucket> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                                                R.layout.list_item_bucket, parent, false);
        return new BucketViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Bucket bucket = data.get(position);

        BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;


        //TODO: check MessageFormat
        // 0 -> 0 shot
        // 1 -> 1 shot
        // 2 -> 2 shots
        String bucketShotCountString = MessageFormat.format(
                holder.itemView.getContext().getResources().getString(R.string.shot_count),
                bucket.shots_count);

        bucketViewHolder.bucketName.setText(bucket.name);
        bucketViewHolder.bucketShotCount.setText(bucketShotCountString);
    }
}
