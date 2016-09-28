package com.tangyang.fribbble.view.shot_detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Shot;

/**
 * Created by YangTang on 9/27/2016.
 */
public class ShotAdapter extends RecyclerView.Adapter {
    public static final int VIEW_TYPE_SHOT_IMAGE = 0;
    public static final int VIEW_TYPE_SHOT_INFO = 1;

    private Shot shot;

    public ShotAdapter(@NonNull Shot shot) {
        this.shot = shot;
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
                // do nothing, just show the inflated view, which is the ImageView in shot_item_image.xml
                break;
            case VIEW_TYPE_SHOT_INFO:
                InfoViewHolder shotDetailViewHolder = (InfoViewHolder) holder;
                shotDetailViewHolder.title.setText(shot.title);
                shotDetailViewHolder.authorName.setText(shot.user.name);
                shotDetailViewHolder.description.setText(shot.description);

                shotDetailViewHolder.likesCount.setText(String.valueOf(shot.likes_count));
                shotDetailViewHolder.bucketsCount.setText(String.valueOf(shot.buckets_count));
                shotDetailViewHolder.viewsCount.setText(String.valueOf(shot.views_count));
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
}
