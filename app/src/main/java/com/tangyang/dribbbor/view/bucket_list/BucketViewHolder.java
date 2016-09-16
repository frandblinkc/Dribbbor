package com.tangyang.dribbbor.view.bucket_list;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangyang.dribbbor.R;
import com.tangyang.dribbbor.base.BaseViewHolder;

import org.w3c.dom.Text;

import butterknife.BindView;

/**
 * Created by tangy on 9/16/2016.
 */
public class BucketViewHolder extends BaseViewHolder {
    @BindView(R.id.bucket_layout) View bucketLaout;
    @BindView(R.id.bucket_name) TextView bucketName;
    @BindView(R.id.bucket_shot_count) TextView bucketShotCount;
    @BindView(R.id.bucket_chosen) ImageView bucketChosen;

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
}
