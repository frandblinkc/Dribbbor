package com.tangyang.fribbble.view.bucket_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.tangyang.fribbble.view.base.SingleFragmentActivity;
import com.tangyang.fribbble.view.shot_list.ShotListFragment;

/**
 * Created by YangTang on 10/6/2016.
 */
public class BucketShotListActivity extends SingleFragmentActivity {
    public static  final String KEY_BUCKET_NAME = "bucket_name";

    @NonNull
    @Override
    protected Fragment newFragment() {
        String bucketId = getIntent().getStringExtra(ShotListFragment.KEY_BUCKET_ID);
        return bucketId == null
                ? ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR)
                : ShotListFragment.newBucketShotListInstance(bucketId);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
       return getIntent().getStringExtra(KEY_BUCKET_NAME);
    }
}
