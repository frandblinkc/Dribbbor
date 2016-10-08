package com.tangyang.fribbble.view.shot_detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.tangyang.fribbble.view.bucket_list.ChooseBucketActivity;

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

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private Shot shot;
    private boolean isLiking;
    private ArrayList<String> collectedBucketIds;


    public static Fragment newInstance(@NonNull Bundle args) {
        ShotFragment fragment = new ShotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (shot == null) {//first time entering shot fragment
            swipeRefreshLayout.setEnabled(false);
            Log.d("frandblinkc", "shot push to refresh disabled!");
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onViewCreated(getView(), null);
            }
        });


        shot = ModelUtils.toObject(getArguments().getString(KEY_SHOT), new TypeToken<Shot>() {
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ShotAdapter(this, shot));



        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        isLiking = true;
        AsyncTaskCompat.executeParallel(new CheckLikeTask());
        AsyncTaskCompat.executeParallel(new LoadBucketsTask());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_BUCKET && resultCode == Activity.RESULT_OK) {
            List<String> chosenBucketIds = data.getStringArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
            List<String> addedBucketIds = new ArrayList<>();
            List<String> removedBucketIds = new ArrayList<>();

            for (String chosenBucketId : chosenBucketIds) {
                if (!collectedBucketIds.contains(chosenBucketId)) {
                    addedBucketIds.add(chosenBucketId);
                }
            }

            for (String collectedBucketId : collectedBucketIds) {
                if (!chosenBucketIds.contains(collectedBucketId)) {
                    removedBucketIds.add(collectedBucketId);
                }
            }

            AsyncTaskCompat.executeParallel(new UpdateCollectedBucketIdsTask(addedBucketIds, removedBucketIds));
        }
    }


    private class LoadBucketsTask extends DribbbleTask<Void, Void, List<String>> {
        @Override
        protected List<String> doJob(Void... params) throws DribbbleException {
            List<Bucket> shotBuckets = Dribbble.getShotBuckets(shot.id);
            List<Bucket> userBuckets = Dribbble.getUserBuckets();

            Set<String> userBucketIds = new HashSet<>();
            for (Bucket userBucket : userBuckets) {
                userBucketIds.add(userBucket.id);
            }

            List<String> collectedBucketIds = new ArrayList<>();
            for (Bucket shotBucket : shotBuckets) {
                if (userBucketIds.contains(shotBucket.id)) {
                    collectedBucketIds.add(shotBucket.id);
                }
            }

            return collectedBucketIds;
        }

        @Override
        protected void onSuccess(List<String> result) {
            collectedBucketIds = new ArrayList<>(result);
            if (result.size() > 0) {
                shot.bucketed = true;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);
            Log.d("frandblinkc", "shot push to refresh enabled!");
        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }


    // Async task to update the list of user's buckets that collected the current shot
    private class UpdateCollectedBucketIdsTask extends DribbbleTask<Void, Void, Void> {
        private List<String> added;
        private List<String> removed;

        public UpdateCollectedBucketIdsTask(@NonNull List<String> added, @NonNull List<String> removed) {
            this.added = added;
            this.removed = removed;
        }

        // Non-UI thread
        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            for (String addedId : added) {
                Dribbble.addBucketShot(addedId, shot.id);
            }

            for (String removedId : removed) {
                Dribbble.removeBucketShot(removedId, shot.id);
            }
            return null;
        }

        // on UI thread
        @Override
        protected void onSuccess(Void aVoid) {
            if (added.isEmpty() && removed.isEmpty()) {
                return;
            }

            collectedBucketIds.addAll(added);
            collectedBucketIds.removeAll(removed);

            shot.bucketed = !collectedBucketIds.isEmpty();
            shot.buckets_count += added.size() - removed.size(); // update locally, no need to reload

            // only update shot detail part, do not load image again
            int last = recyclerView.getAdapter().getItemCount() - 1;
            recyclerView.getAdapter().notifyItemChanged(last);

            setResult();
        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    // async task to check if like current shot or not
    private class CheckLikeTask extends DribbbleTask<Void, Void, Boolean> {
        @Override
        protected Boolean doJob(Void... params) throws DribbbleException {
            return Dribbble.isLikingShot(shot.id);
        }

        @Override
        protected void onSuccess(Boolean result) {
            isLiking = false;
            shot.liked = result;
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        protected void onFailure(DribbbleException e) {
            isLiking = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    // async task to switch the like/unlike status of current shot
    private class LikeTask extends DribbbleTask<Void, Void, Void> {
        private String shotId;
        private boolean like;

        public LikeTask(String shotId, boolean like) {
            this.shotId = shotId;
            this.like = like;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            if (like) {
                Dribbble.likeShot(shotId);
            } else {
                Dribbble.unlikeShot(shotId);
            }
            return null;
        }

        @Override
        protected void onSuccess(Void aVoid) {
            isLiking = false;

            shot.liked = like;
            shot.likes_count += like ? 1 : -1;
            // only update shot detail part, do not load image again
            int last = recyclerView.getAdapter().getItemCount() - 1;
            recyclerView.getAdapter().notifyItemChanged(last);

            setResult();
        }

        @Override
        protected void onFailure(DribbbleException e) {
            isLiking = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }


    // like button onClickListener
    public void like(@NonNull String shotId, boolean like) {
        if (!isLiking) {
            isLiking = true;
            AsyncTaskCompat.executeParallel(new LikeTask(shotId, like));
        }
    }

    // bucketButton onClickListener
    public void bucket() {
        if (collectedBucketIds == null) {
            Snackbar.make(getView(), "Loadin// == null means we are still loadingg buckets, just one seconde...", Snackbar.LENGTH_LONG);
        } else {
            Intent intent = new Intent(getContext(), ChooseBucketActivity.class);
            intent.putStringArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_IDS, collectedBucketIds);
            startActivityForResult(intent, ShotFragment.REQ_CODE_BUCKET);
        }

    }

    // share
    public void share() {
        Log.d("frandblinkc", "sharing shot" + shot.title);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shot.title + " " + shot.html_url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_shot)));
    }

    // set result for ShotActivity, returned to ShotListFragment to update the corresponding item
    private void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_SHOT, ModelUtils.toString(shot, new TypeToken<Shot>() {
        }));
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }


}
