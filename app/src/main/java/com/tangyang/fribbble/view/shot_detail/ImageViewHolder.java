package com.tangyang.fribbble.view.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.tangyang.fribbble.view.base.BaseViewHolder;

/**
 * Created by YangTang on 9/27/2016.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;

    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView;
    }
}
