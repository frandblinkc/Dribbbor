package com.tangyang.fribbble.view.bucket_list;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonSyntaxException;
import com.tangyang.fribbble.R;
import com.tangyang.fribbble.dribbble.Dribbble;
import com.tangyang.fribbble.view.base.LoadMoreListener;
import com.tangyang.fribbble.view.base.SpaceItemDecoration;
import com.tangyang.fribbble.model.Bucket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tangy on 9/15/2016.
 */
public class BucketListFragment extends Fragment{
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;

    private BucketListAdapter adapter;

    public static BucketListFragment newInstance() { return new BucketListFragment(); }

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        adapter = new BucketListAdapter(new ArrayList<Bucket>(), new LoadMoreListener() {
            @Override
            public void onLoadMore() {
                // this method will be called when the RecyclerView is displayed
                // page starts from 1
                // Executes the task with the specified parameters, allowing multiple tasks to run in parallel
                // on a pool of threads managed by {@link android.os.AsyncTask}.
                AsyncTaskCompat.executeParallel(new LoadBucketTask(adapter.getDataCount() / Dribbble.BUCKETS_PER_PAGE + 1));
            }
        });
        recyclerView.setAdapter(adapter);

        // TODO (Yang): fab.setOnClickListener
    }

    private class LoadBucketTask extends AsyncTask<Void, Void, List<Bucket>> {
        int page;

        public LoadBucketTask(int page) {
            this.page = page;
        }

        @Override
        protected List<Bucket> doInBackground(Void... voids) {
            Log.d("frandblinkc", "inside doInBackground of asyncTask loading buckets");
            // this method executed on non-UI thread
            try {
                return Dribbble.getBuckets(page);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Bucket> buckets) {
            // this method is executed on UI thread
            Log.d("frandblinkc", "inside onPostExecute of asyncTask loading buckets");
            if (buckets != null) {
                if (!buckets.isEmpty()) {
                    int k = adapter.getDataCount() % Dribbble.BUCKETS_PER_PAGE;
                    for (int i = 0; i < k; i++){ // resume loading from an incomplete page, simply update current page
                        buckets.remove(0); // remove first k buckets that were already loaded
                    }
                    adapter.append(buckets);
                    adapter.setShowLoading(buckets.size() == Dribbble.BUCKETS_PER_PAGE);
                }
            } else {
                Snackbar.make(getView(), "Error fetching shots!", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
