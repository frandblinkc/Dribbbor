package com.tangyang.fribbble.view.bucket_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Bucket;
import com.tangyang.fribbble.view.base.BaseViewHolder;
import com.tangyang.fribbble.view.base.EndlessListAdapter;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by tangy on 9/16/2016.
 */
public class BucketListAdapter extends EndlessListAdapter<Bucket> {

    public BucketListAdapter(@NonNull Context context,
                             @NonNull List<Bucket> data,
                             @NonNull LoadMoreListener loadMoreListener) {
        super(context, data, loadMoreListener);
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bucket, parent, false);
        return new BucketViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        Bucket bucket = getData().get(position);
        BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;

        bucketViewHolder.bucketName.setText(bucket.name);
        bucketViewHolder.bucketShotCount.setText(formatShotCount(bucket.shots_count));
    }


    private String formatShotCount(int shotCount) {
        return shotCount <= 1
                ? getContext().getString(R.string.shot_count_single, shotCount)
                : getContext().getString(R.string.shot_count_plural, shotCount);
    }
}
