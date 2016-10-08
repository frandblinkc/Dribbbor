package com.tangyang.fribbble.view.shot_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.utils.ImageUtils;
import com.tangyang.fribbble.utils.ModelUtils;
import com.tangyang.fribbble.view.base.BaseViewHolder;
import com.tangyang.fribbble.view.base.EndlessListAdapter;
import com.tangyang.fribbble.view.shot_detail.ShotActivity;
import com.tangyang.fribbble.view.shot_detail.ShotFragment;

import java.util.List;


/**
 * Created by tangy on 9/14/2016.
 */
public class ShotListAdapter extends EndlessListAdapter<Shot> {
    private final ShotListFragment shotListFragment;

    public ShotListAdapter(@NonNull ShotListFragment shotListFragment,
                           @NonNull List<Shot> data,
                           @NonNull LoadMoreListener loadMoreListener) {
        super(shotListFragment.getContext(), data, loadMoreListener);
        this.shotListFragment = shotListFragment;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_shot, parent, false);
        return new ShotViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        final Shot shot = getData().get(position);
        ShotViewHolder shotViewHolder = (ShotViewHolder) holder;

        // prevent Uri.parse(null) error
        Log.d("frandblinkc", "loading url: " + shot.getImageUrl());
        if (shot.getImageUrl() == null) {
            shotViewHolder.itemView.setVisibility(View.GONE);
            return;
        }

        shotViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
        shotViewHolder.viewCount.setText(String.valueOf(shot.views_count));
        shotViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));

        // show gif label for .gif files
        if (shot.getImageUrl().indexOf(".gif") != -1) {
            shotViewHolder.gifLabel.setVisibility(View.VISIBLE);
        }

        // load image
        ImageUtils.loadImage(shot, shotViewHolder.image);



        shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShotActivity.class);
                intent.putExtra(ShotFragment.KEY_SHOT,
                        ModelUtils.toString(shot, new TypeToken<Shot>(){}));
                intent.putExtra(ShotActivity.KEY_SHOT_TITLE, shot.title);
                shotListFragment.startActivityForResult(intent,ShotListFragment.REQ_CODE_SHOT);
            }
        });
    }

}
