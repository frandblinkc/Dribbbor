package com.tangyang.fribbble.view.bucket_list;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tangy on 9/15/2016.
 */
public class BucketListFragment extends Fragment{
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;

    private BucketListAdapter adapter;

    private EndlessListAdapter.LoadMoreListener loadMoreListener = new EndlessListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            AsyncTaskCompat.executeParallel(new LoadBucketTask());
        }
    };


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

        adapter = new BucketListAdapter(getContext(), new ArrayList<Bucket>(), loadMoreListener);
        recyclerView.setAdapter(adapter);

        // TODO (Yang): fab.setOnClickListener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewBucketDialogFragment dialogFragment = NewBucketDialogFragment.newInstance();
                dialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
            }
        });
    }

    private class LoadBucketTask extends DribbbleTask<Void, Void, List<Bucket>> {

        @Override
        protected List<Bucket> doJob(Void... params) throws DribbbleException {
            final int page = adapter.getData().size() / Dribbble.BUCKETS_PER_PAGE + 1;
            return Dribbble.getUserBuckets(page);
        }

        @Override
        protected void onSuccess(List<Bucket> buckets) {
            adapter.setShowLoading(buckets.size() >= Dribbble.BUCKETS_PER_PAGE);
            int k = adapter.getData().size() % Dribbble.BUCKETS_PER_PAGE;
            for (int i = 0; i < k; i++){ // resume loading from an incomplete page, simply update current page
                buckets.remove(0); // remove first k buckets that were already loaded
            }
            adapter.append(buckets);
        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }


    }
}
