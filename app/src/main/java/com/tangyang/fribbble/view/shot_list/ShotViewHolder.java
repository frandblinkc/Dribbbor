package com.tangyang.fribbble.view.shot_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tangyang.fribbble.R;
import com.tangyang.fribbble.view.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by tangy on 9/14/2016.
 */
public class ShotViewHolder extends BaseViewHolder {
    @BindView(R.id.shot_clickable_cover) View cover;
    @BindView(R.id.shot_like_count) TextView likeCount;
    @BindView(R.id.shot_view_count) TextView viewCount;
    @BindView(R.id.shot_bucket_count) TextView bucketCount;
    @BindView(R.id.list_shot_image) SimpleDraweeView image;
    @BindView(R.id.gif_label) View gifLabel;


    public ShotViewHolder(View itemView) {
        super(itemView);
    }
}
