package com.tangyang.fribbble.view.shot_list;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tangyang.fribbble.R;
import com.tangyang.fribbble.dribbble.Dribbble;
import com.tangyang.fribbble.dribbble.DribbbleException;
import com.tangyang.fribbble.utils.ModelUtils;
import com.tangyang.fribbble.utils.custom_behavior.ScrollUpShowBehavior;
import com.tangyang.fribbble.view.base.DribbbleTask;
import com.tangyang.fribbble.view.base.EndlessListAdapter;
import com.tangyang.fribbble.view.base.SpaceItemDecoration;
import com.tangyang.fribbble.model.Shot;
import com.tangyang.fribbble.view.shot_detail.ShotFragment;

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
    public static final int LIST_TYPE_LIKED = 1;
    public static final int LIST_TYPE_BUCKET = 2;

    public static final int REQ_CODE_SHOT = 100;


    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    FloatingActionButton fab;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    private ShotListAdapter adapter;
    private int listType;
    private LinearLayoutManager linearLayoutManager;



    private EndlessListAdapter.LoadMoreListener loadMoreListener = new EndlessListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedIn()) {
                AsyncTaskCompat.executeParallel(new LoadShotTask(false));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SHOT && resultCode == Activity.RESULT_OK) {
            Shot updatedShot = ModelUtils.toObject(data.getStringExtra(ShotFragment.KEY_SHOT),
                                                                new TypeToken<Shot>(){});

            List<Shot> shots = adapter.getData();
            for (int i = 0; i < shots.size(); i++) {
                Shot shot = shots.get(i);
                if (TextUtils.equals(shot.id, updatedShot.id)) {
                    if (shot.likes_count == updatedShot.likes_count
                            && shot.buckets_count == updatedShot.buckets_count) {
                        return;
                    }
                    shot.likes_count = updatedShot.likes_count;
                    shot.buckets_count = updatedShot.buckets_count;

                    if (!shot.liked) {
                        adapter.removeDataItem(i);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

        }
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

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadShotTask(true));
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_small)));

        adapter = new ShotListAdapter(this, new ArrayList<Shot>(), loadMoreListener);

        recyclerView.setAdapter(adapter);

        // set up the fab back to top listener
        fab = (FloatingActionButton) this.getActivity().findViewById(R.id.fab);

        // set the ImageDrawable and behavior
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_upward_white_24dp));
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setBehavior(new ScrollUpShowBehavior());
        fab.setLayoutParams(p);

        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutManager.scrollToPosition(0);
            }
        });
    }





    private class LoadShotTask extends DribbbleTask<Void, Void, List<Shot>> {
        private boolean refresh;

        public LoadShotTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doJob(Void... params) throws DribbbleException {
            int page = refresh? 1: adapter.getData().size() / Dribbble.SHOTS_PER_PAGE + 1;
            switch(listType) {
                case LIST_TYPE_POPULAR:
                    return Dribbble.getShots(page);
                case LIST_TYPE_LIKED:
                    return Dribbble.getLikedShots(page);
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

            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(shots);
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(shots);
            }

        }

        @Override
        protected void onFailure(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

}
