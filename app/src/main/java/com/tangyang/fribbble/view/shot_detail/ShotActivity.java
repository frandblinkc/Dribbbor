package com.tangyang.fribbble.view.shot_detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tangyang.fribbble.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YangTang on 9/27/2016.
 */
public class ShotActivity extends AppCompatActivity{
    public static final String KEY_SHOT_TITLE = "shot title";

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Shot detail");

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, newFragment())
                        .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    protected Fragment newFragment() {
        return ShotFragment.newInstance(getIntent().getExtras());
    }

    @NonNull
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }

}
