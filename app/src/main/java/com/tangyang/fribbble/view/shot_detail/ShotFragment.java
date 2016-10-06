package com.tangyang.fribbble.view.shot_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.tangyang.fribbble.R;
import com.tangyang.fribbble.dribbble.Dribbble;
import com.tangyang.fribbble.dribbble.DribbbleException;
import com.tangyang.fribbble.model.Bucket;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.utils.ModelUtils;
import com.tangyang.fribbble.view.base.DribbbleTask;
import com.tangyang.fribbble.view.bucket_list.BucketListFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YangTang on 9/27/2016.
 */
public class ShotFragment extends Fragment {
    public static final String KEY_SHOT = "shot";
    public static final int REQ_CODE_BUCKET = 100;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Shot shot;
    private ShotAdapter shotAdapter;

    public static Fragment newInstance(@NonNull Bundle args) {
        ShotFragment fragment = new ShotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        shot = ModelUtils.toObject(getArguments().getString(KEY_SHOT), new TypeToken<Shot>(){});
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shotAdapter = new ShotAdapter(this, shot);
        recyclerView.setAdapter(shotAdapter);

        AsyncTaskCompat.executeParallel(new LoadCollectedBucketIdsTask());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_BUCKET && resultCode == Activity.RESULT_OK) {
            List<String> chosenBucketIds = data.getStringArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
            List<String> addedBucketIds = new ArrayList<>();
            List<String> removedBucketIds = new ArrayList<>();
            List<String> collectedBucketIds = shotAdapter.getReadOnlyCollectedBucketIds();

            for (String chosenBucketId: chosenBucketIds) {
                if (!collectedBucketIds.contains(chosenBucketId)) {
                    addedBucketIds.add(chosenBucketId);
                }
            }

            for (String collectedBucketId: collectedBucketIds) {
                if (!chosenBucketIds.contains(collectedBucketId)) {
                    removedBucketIds.add(collectedBucketId);
                }
            }

            AsyncTaskCompat.executeParallel(new UpdateCollectedBucketIdsTask(addedBucketIds, removedBucketIds));
        }
    }

    private class LoadCollectedBucketIdsTask extends DribbbleTask<Void, Void, List<String>> {
        @Override
        protected List<String> doJob(Void... params) throws DribbbleException {
            List<Bucket> shotBuckets = Dribbble.getShotBuckets(shot.id);
            List<Bucket> userBuckets = Dribbble.getUserBuckets();

            Set<String> userBucketIds = new HashSet<>();
            for (Bucket userBucket: userBuckets) {
                userBucketIds.add(userBucket.id);
            }

            List<String> collectedBucketIds = new ArrayList<>();
            for (Bucket shotBucket: shotBuckets) {
                if (userBucketIds.contains(shotBucket.id)) {
                    collectedBucketIds.add(shotBucket.id);
                }
            }

            return collectedBucketIds;
        }

        @Override
        protected void onSuccess(List<String> collectedBucketIds) {
                shotAdapter.updateCollectedBucketIds(collectedBucketIds);
        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG);
        }
    }


    // Async task to update the list of user's buckets that collected the current shot
    private class UpdateCollectedBucketIdsTask extends  DribbbleTask<Void, Void, Void> {
        private List<String> added;
        private List<String> removed;

        public UpdateCollectedBucketIdsTask(@NonNull List<String> added, @NonNull List<String> removed) {
            this.added = added;
            this.removed = removed;
        }

        // Non-UI thread
        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            for (String addedId: added) {
                Dribbble.addBucketShot(addedId, shot.id);
            }

            for (String removedId: removed) {
                Dribbble.removeBucketShot(removedId, shot.id);
            }
            return null;
        }

        // on UI thread
        @Override
        protected void onSuccess(Void aVoid) {
            shotAdapter.updateCollectedBucketIds(added, removed);
        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG);
        }
    }


}
