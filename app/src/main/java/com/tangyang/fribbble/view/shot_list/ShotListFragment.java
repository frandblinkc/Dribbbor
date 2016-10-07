package com.tangyang.fribbble.view.shot_list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.tangyang.fribbble.dribbble.DribbbleException;
import com.tangyang.fribbble.view.base.DribbbleTask;
import com.tangyang.fribbble.view.base.EndlessListAdapter;
import com.tangyang.fribbble.view.base.SpaceItemDecoration;
import com.tangyang.fribbble.model.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tangy on 9/13/2016.
 */
public class ShotListFragment extends Fragment {

    public static final String KEY_BUCKET_ID = "bucket_id";
    public static final String KEY_LIST_TYPE = "list_type";

    public static final int LIST_TYPE_POPULAR = 0;
    public static final int LIST_TYPE_LIKEED = 1;
    public static final int LIST_TYPE_BUCKET = 2;


    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private ShotListAdapter adapter;
    private int listType;

    private EndlessListAdapter.LoadMoreListener loadMoreListener = new EndlessListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedIn()) {
                AsyncTaskCompat.executeParallel(new LoadShotTask());
            }
        }
    };



    public static ShotListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ShotListFragment newBucketShotListInstance(@NonNull String bucketId) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_BUCKET);
        args.putString(KEY_BUCKET_ID, bucketId);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    // create fragment View objects hierarchy
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view); // equivalent to this.recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        return view;
    }


    @Override
    // Any view setup should occur here, e.g. view lookups and attaching view listeners
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listType = getArguments().getInt(KEY_LIST_TYPE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_small)));

        adapter = new ShotListAdapter(this, new ArrayList<Shot>(), loadMoreListener);

        recyclerView.setAdapter(adapter);
    }



    private class LoadShotTask extends DribbbleTask<Void, Void, List<Shot>> {

        @Override
        protected List<Shot> doJob(Void... params) throws DribbbleException {
            int page = adapter.getData().size() / Dribbble.SHOTS_PER_PAGE + 1;
            switch(listType) {
                case LIST_TYPE_POPULAR:
                    return Dribbble.getShots(page);
                case LIST_TYPE_BUCKET:
                    String bucketId = getArguments().getString(KEY_BUCKET_ID);
                    return Dribbble.getBucketShots(bucketId, page);
                default:
                    return Dribbble.getShots(page);
            }
        }

        @Override
        protected void onSuccess(List<Shot> shots) {
            adapter.setShowLoading(shots.size() >= Dribbble.SHOTS_PER_PAGE);
            int k = adapter.getData().size() % Dribbble.SHOTS_PER_PAGE;
            for (int i = 0; i < k; i++){ // resume loading from an incomplete page, simply update current page
                shots.remove(0); // remove first k buckets that were already loaded
            }
            adapter.append(shots);
        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

}
