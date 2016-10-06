package com.tangyang.fribbble.view.bucket_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.view.base.SingleFragmentActivity;

/**
 * Created by YangTang on 10/6/2016.
 */
public class ChooseBucketActivity extends SingleFragmentActivity {

    @NonNull
    @Override
    protected Fragment newFragment() {
        return BucketListFragment.newInstance(true);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getString(R.string.choose_bucket);
    }
}
