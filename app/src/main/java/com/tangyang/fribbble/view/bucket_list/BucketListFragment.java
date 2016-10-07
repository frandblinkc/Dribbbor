package com.tangyang.fribbble.view.bucket_list;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.tangyang.fribbble.R;
import com.tangyang.fribbble.dribbble.Dribbble;
import com.tangyang.fribbble.dribbble.DribbbleException;
import com.tangyang.fribbble.view.base.DribbbleTask;
import com.tangyang.fribbble.view.base.EndlessListAdapter;
import com.tangyang.fribbble.view.base.SpaceItemDecoration;
import com.tangyang.fribbble.model.Bucket;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tangy on 9/15/2016.
 */
public class BucketListFragment extends Fragment{
    public static final int REQ_CODE_NEW_BUCKET = 100;

    public static final String KEY_CHOOSING_MODE = "choosing_mode";
    public static final String KEY_CHOSEN_BUCKET_IDS = "chosen_bucket_ids";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    private BucketListAdapter adapter;
    private boolean isChoosingMode;
    private ArrayList<String> chosenBucketIds;

    private EndlessListAdapter.LoadMoreListener loadMoreListener = new EndlessListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            AsyncTaskCompat.executeParallel(new LoadBucketTask(false));
        }
    };


    public static BucketListFragment newInstance(boolean isChoosingMode,
                                                 @Nullable ArrayList<String> chosenBucketIds ) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_CHOOSING_MODE, isChoosingMode);
        args.putStringArrayList(KEY_CHOSEN_BUCKET_IDS, chosenBucketIds);

        BucketListFragment bucketListFragment = new BucketListFragment();
        bucketListFragment.setArguments(args);
        return bucketListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fab_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        isChoosingMode = getArguments().getBoolean(KEY_CHOOSING_MODE);
        if (isChoosingMode) {
            chosenBucketIds = getArguments().getStringArrayList(KEY_CHOSEN_BUCKET_IDS);
            if (chosenBucketIds == null) {
                chosenBucketIds = new ArrayList<>();
            }
        }

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadBucketTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        adapter = new BucketListAdapter(getContext(), new ArrayList<Bucket>(), loadMoreListener, isChoosingMode);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewBucketDialogFragment dialogFragment = NewBucketDialogFragment.newInstance();
                dialogFragment.setTargetFragment(BucketListFragment.this, REQ_CODE_NEW_BUCKET);
                dialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isChoosingMode) {
            inflater.inflate(R.menu.bucket_list_choose_mode_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            ArrayList<String> chosenBucketIds = adapter.getSelectedBucketIds();
            Log.d("frandblinkc", "returning from ChooseBucketActivity with chosen buckets number: " + chosenBucketIds.size());
            Intent intent = new Intent();
            intent.putStringArrayListExtra(KEY_CHOSEN_BUCKET_IDS, chosenBucketIds);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_NEW_BUCKET && resultCode == Activity.RESULT_OK) {
            String bucketName = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_NAME);
            String bucketDescription = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_DESCRIPTION);
            if (!TextUtils.isEmpty(bucketName)) {
                AsyncTaskCompat.executeParallel(new NewBucketTask(bucketName, bucketDescription));
            }
        }
    }

    private class LoadBucketTask extends DribbbleTask<Void, Void, List<Bucket>> {
        private boolean refresh;

        public LoadBucketTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Bucket> doJob(Void... params) throws DribbbleException {
            final int page = refresh? 1: adapter.getData().size() / Dribbble.BUCKETS_PER_PAGE + 1;
            return Dribbble.getUserBuckets(page);
        }

        @Override
        protected void onSuccess(List<Bucket> buckets) {
            if (buckets == null) {
                return;
            }

            if (isChoosingMode) {
                for (Bucket bucket: buckets) {
                    if (chosenBucketIds.contains(bucket.id)) {
                        bucket.isChosen = true;
                    }
                }
            }
            adapter.setShowLoading(buckets.size() >= Dribbble.BUCKETS_PER_PAGE);
            int k = adapter.getData().size() % Dribbble.BUCKETS_PER_PAGE;
            for (int i = 0; i < k; i++){ // resume loading from an incomplete page, simply update current page
                buckets.remove(0); // remove first k buckets that were already loaded
            }

            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(buckets);
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(buckets);
            }
        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class NewBucketTask extends DribbbleTask<Void, Void, Bucket> {
        private String name;
        private String description;

        public NewBucketTask(String name, String description) {
            this.name = name;
            this.description = description;
        }

        @Override
        protected Bucket doJob(Void... params) throws DribbbleException {
            return Dribbble.newBucket(name, description);
        }

        @Override
        protected void onSuccess(Bucket bucket) {
            if (bucket != null) {
                adapter.prepend(Collections.singletonList(bucket));
            } else {
                Snackbar.make(getView(), "Creating bucket failed!", Snackbar.LENGTH_LONG);
            }
        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG);
        }
    }
}
