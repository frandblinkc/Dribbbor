package com.tangyang.fribbble.view.shot_detail;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.utils.ImageUtils;
import com.tangyang.fribbble.view.bucket_list.BucketListFragment;
import com.tangyang.fribbble.view.bucket_list.ChooseBucketActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by YangTang on 9/27/2016.
 */
public class ShotAdapter extends RecyclerView.Adapter {
    public static final int VIEW_TYPE_SHOT_IMAGE = 0;
    public static final int VIEW_TYPE_SHOT_INFO = 1;

    private final Shot shot;
    private final ShotFragment shotFragment;
    private ArrayList<String> collectedBucketIds;

    public ShotAdapter(@NonNull ShotFragment shotFragment, @NonNull Shot shot) {
        this.shotFragment = shotFragment;
        this.shot = shot;
        this.collectedBucketIds = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.shot_item_image, parent, false);
                return new ImageViewHolder(view);
            case VIEW_TYPE_SHOT_INFO:
                view = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.shot_item_info,parent, false);
                return new InfoViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch(viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                ImageUtils.loadImage(shot, ((ImageViewHolder) holder).image);
                break;
            case VIEW_TYPE_SHOT_INFO:
                InfoViewHolder shotDetailViewHolder = (InfoViewHolder) holder;
                shotDetailViewHolder.title.setText(shot.title);
                shotDetailViewHolder.authorName.setText(shot.user.name);
                shotDetailViewHolder.description.setText(shot.description);

                shotDetailViewHolder.likesCount.setText(String.valueOf(shot.likes_count));
                shotDetailViewHolder.bucketsCount.setText(String.valueOf(shot.buckets_count));
                shotDetailViewHolder.viewsCount.setText(String.valueOf(shot.views_count));


                shotDetailViewHolder.bucketButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bucket(view.getContext());
                    }
                });
                break;
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_SHOT_IMAGE;
        } else {
            return VIEW_TYPE_SHOT_INFO;
        }
    }


    private void bucket(Context context) {
        if (collectedBucketIds != null) {// == null means we are still loading
            Intent intent = new Intent(context, ChooseBucketActivity.class);

            intent.putStringArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_IDS, collectedBucketIds);
            shotFragment.startActivityForResult(intent, ShotFragment.REQ_CODE_BUCKET);
        }

    }

    // update the list of collected bucket ids for current shot
    public void updateCollectedBucketIds(@NonNull List<String> bucketIds) {
        if (collectedBucketIds == null) {
            collectedBucketIds = new ArrayList<>();
        }

        collectedBucketIds.clear();
        collectedBucketIds.addAll(bucketIds);

        shot.bucketed = !bucketIds.isEmpty();
        notifyDataSetChanged();
    }

    // update the list of collected bucket ids for current shot, with addedIds and removedIds
    public void updateCollectedBucketIds(List<String> addedIds, List<String> removedIds) {
        if (collectedBucketIds == null) {
            collectedBucketIds = new ArrayList<>();
        }

        if (addedIds.isEmpty() && removedIds.isEmpty()) {
            return;
        }

        collectedBucketIds.addAll(addedIds);
        collectedBucketIds.removeAll(removedIds);

        shot.bucketed = !collectedBucketIds.isEmpty();
        shot.buckets_count += addedIds.size() - removedIds.size(); // update locally, no need to reload
        notifyDataSetChanged();
    }

    public List<String> getReadOnlyCollectedBucketIds() {
        return Collections.unmodifiableList(collectedBucketIds);
    }
}
