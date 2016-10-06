package com.tangyang.fribbble.view.shot_list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.tangyang.fribbble.R;
import com.tangyang.fribbble.dribbble.Dribbble;
import com.tangyang.fribbble.model.User;
import com.tangyang.fribbble.view.base.LoadMoreListener;
import com.tangyang.fribbble.view.base.SpaceItemDecoration;
import com.tangyang.fribbble.model.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tangy on 9/13/2016.
 */
public class ShotListFragment extends Fragment {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private ShotListAdapter adapter;

    public static ShotListFragment newInstance() {
        return new ShotListFragment();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        adapter = new ShotListAdapter(new ArrayList<Shot>(), new LoadMoreListener() {
            @Override
            public void onLoadMore() {
                // this method will be called when the RecyclerView is displayed
                // page starts from 1
                // Executes the task with the specified parameters, allowing multiple tasks to run in parallel
                // on a pool of threads managed by {@link android.os.AsyncTask}.
                AsyncTaskCompat.executeParallel(new LoadShotTask(adapter.getDataCount() / Dribbble.SHOTS_PER_PAGE + 1));
            }
        });

        recyclerView.setAdapter(adapter);
    }



    private class LoadShotTask extends AsyncTask<Void, Void, List<Shot>> {
        int page;

        public LoadShotTask(int page) {
            this.page = page;
        }

        @Override
        protected List<Shot> doInBackground(Void... voids) {
            Log.d("frandblinkc", "inside doInBackground of asyncTask");
            // this method executed on non-UI thread
            try {
                return Dribbble.getShots(page);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Shot> shots) {
            // this method is executed on UI thread
            Log.d("frandblinkc", "inside onPostExecute of asyncTask");
            if (shots != null) {
                if (!shots.isEmpty()) {
                    adapter.append(shots);
                    adapter.setShowLoading(shots.size() == Dribbble.SHOTS_PER_PAGE);
                }
            } else {
                Snackbar.make(getView(), "Error fetching shots!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

}
