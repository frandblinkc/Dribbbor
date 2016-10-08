package com.tangyang.fribbble.view.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tangyang.fribbble.view.base.BaseViewHolder;

/**
 * Created by YangTang on 9/27/2016.
 */
public class ShotImageViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView image;

    public ShotImageViewHolder(View itemView) {
        super(itemView);
        image = (SimpleDraweeView) itemView;
    }
}
