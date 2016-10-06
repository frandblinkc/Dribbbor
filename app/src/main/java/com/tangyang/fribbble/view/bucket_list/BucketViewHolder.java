package com.tangyang.fribbble.view.bucket_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.view.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by tangy on 9/16/2016.
 */
public class BucketViewHolder extends BaseViewHolder {
    @BindView(R.id.bucket_layout) View bucketLayout;
    @BindView(R.id.bucket_name) TextView bucketName;
    @BindView(R.id.bucket_shot_count) TextView bucketShotCount;
    @BindView(R.id.bucket_chosen) ImageView bucketChosen;

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
}
