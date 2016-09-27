package com.tangyang.fribbble.view.shot_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.view.shot_detail.ShotActivity;

import java.util.List;


/**
 * Created by tangy on 9/14/2016.
 */
public class ShotListAdapter extends RecyclerView.Adapter {

    private List<Shot> data;

    public ShotListAdapter(@NonNull  List<Shot> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.list_item_shot, parent, false);
        return new ShotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Shot shot = data.get(position);

        ShotViewHolder shotViewHolder = (ShotViewHolder) holder;
        shotViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
        shotViewHolder.viewCount.setText(String.valueOf(shot.views_count));
        shotViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
        shotViewHolder.image.setImageResource(R.drawable.shot_placeholder);

        shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, ShotActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
