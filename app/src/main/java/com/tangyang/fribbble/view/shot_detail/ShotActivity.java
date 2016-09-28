package com.tangyang.fribbble.view.shot_detail;


import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;



import com.tangyang.fribbble.view.base.SingleFragmentActivity;


/**
 * Created by YangTang on 9/27/2016.
 */
public class ShotActivity extends SingleFragmentActivity{
    public static final String KEY_SHOT_TITLE = "shot title";

    @NonNull
    @Override
    protected Fragment newFragment() {
        return ShotFragment.newInstance(getIntent().getExtras());
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }

}
