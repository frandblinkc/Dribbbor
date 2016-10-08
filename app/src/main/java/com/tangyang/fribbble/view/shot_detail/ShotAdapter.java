package com.tangyang.fribbble.view.shot_detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.utils.ImageUtils;

/**
 * Created by YangTang on 9/27/2016.
 */
public class ShotAdapter extends RecyclerView.Adapter {
    public static final int VIEW_TYPE_SHOT_IMAGE = 0;
    public static final int VIEW_TYPE_SHOT_DETAIL = 1;

    private final Shot shot;
    private final ShotFragment shotFragment;


    public ShotAdapter(@NonNull ShotFragment shotFragment, @NonNull Shot shot) {
        this.shotFragment = shotFragment;
        this.shot = shot;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.shot_item_image, parent, false);
                return new ShotImageViewHolder(view);
            case VIEW_TYPE_SHOT_DETAIL:
                view = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.shot_item_detail,parent, false);
                return new ShotDetailViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch(viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                ImageUtils.loadImage(shot, ((ShotImageViewHolder) holder).image);
                break;
            case VIEW_TYPE_SHOT_DETAIL:
                ShotDetailViewHolder shotDetailViewHolder = (ShotDetailViewHolder) holder;
                shotDetailViewHolder.title.setText(shot.title);
                shotDetailViewHolder.authorName.setText(shot.user.name);

                shotDetailViewHolder.description.setText(Html.fromHtml(shot.description == null? "": shot.description));
                shotDetailViewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());


                shotDetailViewHolder.likesCount.setText(String.valueOf(shot.likes_count));
                shotDetailViewHolder.bucketsCount.setText(String.valueOf(shot.buckets_count));
                shotDetailViewHolder.viewsCount.setText(String.valueOf(shot.views_count));

                //TODO: load user image

                shotDetailViewHolder.likesCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Likes count clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                shotDetailViewHolder.bucketsCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Buckets count clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                shotDetailViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotFragment.like(shot.id, !shot.liked);
                    }
                });

                shotDetailViewHolder.bucketButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotFragment.bucket();
                    }
                });

                shotDetailViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotFragment.share();
                    }
                });

                Drawable likeDrawable = shot.liked
                                    ? ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_dribbble_18dp)
                                    : ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_black_18dp);
                shotDetailViewHolder.likeButton.setImageDrawable(likeDrawable);
                Drawable bucketDrawable = shot.bucketed
                                    ? ContextCompat.getDrawable(getContext(), R.drawable.ic_inbox_dribbble_18dp)
                                    : ContextCompat.getDrawable(getContext(), R.drawable.ic_inbox_black_18dp);
                shotDetailViewHolder.bucketButton.setImageDrawable(bucketDrawable);

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
            return VIEW_TYPE_SHOT_DETAIL;
        }
    }



    @NonNull
    private Context getContext() {
        return shotFragment.getContext();
    }
}
