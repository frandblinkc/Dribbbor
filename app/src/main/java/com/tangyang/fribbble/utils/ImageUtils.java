package com.tangyang.fribbble.utils;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.tangyang.fribbble.R;
import com.tangyang.fribbble.model.Shot;

/**
 * Created by YangTang on 10/5/2016.
 */
public class ImageUtils {

    public static SimpleDraweeView loadImage(String url, SimpleDraweeView image) {
        // set progress JPEG image
        Uri uri = Uri.parse(url);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();
        // gif control
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();

        image.setController(controller);
        return image;
    }


    public static void loadShotImage(Shot shot, SimpleDraweeView image) {
        loadImage(shot.getImageUrl(), image);

        addProgressBar(image);
    }

    public static void addProgressBar(SimpleDraweeView image) {
        ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
        progressBarDrawable.setColor(ContextCompat.getColor(image.getContext(), R.color.colorPrimary));

        image.getHierarchy().setProgressBarImage(progressBarDrawable);
    }
}