package com.tangyang.fribbble.view.shot_list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.utils.ModelUtils;
import com.tangyang.fribbble.view.shot_detail.ShotActivity;
import com.tangyang.fribbble.view.shot_detail.ShotFragment;

import java.util.List;


/**
 * Created by tangy on 9/14/2016.
 */
public class ShotListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_SHOT = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Shot> data;
    private LoadMoreListener loadMoreListener;
    private boolean showLoading;

    public ShotListAdapter(@NonNull  List<Shot> data, @NonNull LoadMoreListener loadMoreListener) {
        this.data = data;
        this.loadMoreListener = loadMoreListener;
        this.showLoading = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SHOT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_shot, parent, false);
            return new ShotViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_loading, parent, false);
            return new RecyclerView.ViewHolder(view) {}; //create an anonymous subclass and instantiate it
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // fire onLoadMore() at the beginning when starting to display last item
        if (data.size() == 0 || position == data.size() - 1) {
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            return;
        }

        final Shot shot = data.get(position);
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

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(shot.getImageUrl()))
                .setAutoPlayAnimations(true)
                .build();

        shotViewHolder.image.setController(controller);


        shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, ShotActivity.class);
                intent.putExtra(ShotFragment.KEY_SHOT,
                        ModelUtils.toString(shot, new TypeToken<Shot>(){}));
                intent.putExtra(ShotActivity.KEY_SHOT_TITLE, shot.title);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showLoading? data.size() + 1: data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < data.size()? VIEW_TYPE_SHOT: VIEW_TYPE_LOADING;
    }

    public void append(@NonNull List<Shot> moreShots) {
        data.addAll(moreShots);
        notifyDataSetChanged();
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }


    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public int getDataCount() {
        return data.size();
    }
}
