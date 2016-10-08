package com.tangyang.fribbble.view.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;

import com.tangyang.fribbble.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YangTang on 10/7/2016.
 */
public abstract class SingleFragmentFabActivity extends SingleFragmentActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment_fab);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getActivityTitle());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, newFragment())
                    .commit();
        }


    }


}
