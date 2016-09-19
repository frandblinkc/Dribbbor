package com.tangyang.fribbble.view.shot_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.view.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by tangy on 9/14/2016.
 */
public class ShotViewHolder extends BaseViewHolder {
    @BindView(R.id.shot_like_count) public TextView likeCount;
    @BindView(R.id.shot_view_count) public TextView viewCount;
    @BindView(R.id.shot_bucket_count) public TextView bucketCount;
    @BindView(R.id.list_shot_image) public ImageView image;


    public ShotViewHolder(View itemView) {
        super(itemView);
    }
}
