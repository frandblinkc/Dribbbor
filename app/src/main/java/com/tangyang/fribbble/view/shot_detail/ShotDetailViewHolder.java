package com.tangyang.fribbble.view.shot_detail;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.view.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by YangTang on 9/27/2016.
 */
public class ShotDetailViewHolder extends BaseViewHolder {

    @BindView(R.id.shot_title) TextView title;
    @BindView(R.id.shot_description) TextView description;
    @BindView(R.id.shot_author_image) ImageView authorImage;
    @BindView(R.id.shot_author_name) TextView authorName;
    @BindView(R.id.shot_likes_count) TextView likesCount;
    @BindView(R.id.shot_views_count) TextView viewsCount;
    @BindView(R.id.shot_buckets_count) TextView bucketsCount;
    @BindView(R.id.shot_action_like) ImageButton likeButton;
    @BindView(R.id.shot_action_bucket) ImageButton bucketButton;
    @BindView(R.id.shot_action_share) TextView shareButton;



    public ShotDetailViewHolder(View itemView) {
        super(itemView);
    }
}
