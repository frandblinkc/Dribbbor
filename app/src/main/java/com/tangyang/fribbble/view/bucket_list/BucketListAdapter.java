package com.tangyang.fribbble.view.bucket_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Bucket;
import com.tangyang.fribbble.view.base.BaseViewHolder;
import com.tangyang.fribbble.view.base.EndlessListAdapter;
import com.tangyang.fribbble.view.shot_list.ShotListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangy on 9/16/2016.
 */
public class BucketListAdapter extends EndlessListAdapter<Bucket> {
    private boolean isChoosingMode;

    public BucketListAdapter(@NonNull Context context,
                             @NonNull List<Bucket> data,
                             @NonNull LoadMoreListener loadMoreListener,
                             boolean isChoosingMode) {
        super(context, data, loadMoreListener);
        this.isChoosingMode = isChoosingMode;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bucket, parent, false);
        return new BucketViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, final int position) {
        final Bucket bucket = getData().get(position);
        BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;
        final Context context = holder.itemView.getContext();

        bucketViewHolder.bucketName.setText(bucket.name);
        bucketViewHolder.bucketShotCount.setText(formatShotCount(bucket.shots_count));

        if (isChoosingMode) {
            bucketViewHolder.bucketChosen.setVisibility(View.VISIBLE);
            bucketViewHolder.bucketChosen.setImageDrawable(
                    bucket.isChosen
                                ? ContextCompat.getDrawable(context, R.drawable.ic_check_box_black_36dp)
                                : ContextCompat.getDrawable(context, R.drawable.ic_check_box_outline_blank_black_36dp));
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bucket.isChosen = !bucket.isChosen;
                    Log.d("frandblinkc", "change chosen state for position: " + position);
                    notifyItemChanged(position);
                }
            });
        } else {
            bucketViewHolder.bucketChosen.setVisibility(View.GONE);
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), BucketShotListActivity.class);
                    intent.putExtra(ShotListFragment.KEY_BUCKET_ID, bucket.id);
                    intent.putExtra(BucketShotListActivity.KEY_BUCKET_NAME, bucket.name);
                    getContext().startActivity(intent);
                }
            });
        }
    }


    private String formatShotCount(int shotCount) {
        return shotCount <= 1
                ? getContext().getString(R.string.shot_count_single, shotCount)
                : getContext().getString(R.string.shot_count_plural, shotCount);
    }

    public ArrayList<String> getSelectedBucketIds() {
        ArrayList<String> selectedBucketIds = new ArrayList<>();
        for (Bucket bucket: getData()) {
            if (bucket.isChosen) {
                selectedBucketIds.add(bucket.id);
            }
        }
        return selectedBucketIds;
    }


}
