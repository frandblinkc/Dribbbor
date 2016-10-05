package com.tangyang.fribbble;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by YangTang on 10/5/2016.
 */
public class FribbbleApplicatioin extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
