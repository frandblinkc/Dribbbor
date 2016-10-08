package com.tangyang.fribbble.view.bucket_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.view.base.SingleFragmentFabActivity;

import java.util.ArrayList;

/**
 * Created by YangTang on 10/6/2016.
 */
public class ChooseBucketActivity extends SingleFragmentFabActivity {

    @NonNull
    @Override
    protected Fragment newFragment() {
        ArrayList<String> chosenBucketIds = getIntent().getStringArrayListExtra(
                            BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
        return BucketListFragment.newInstance(true, chosenBucketIds);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getString(R.string.choose_bucket);
    }
}
