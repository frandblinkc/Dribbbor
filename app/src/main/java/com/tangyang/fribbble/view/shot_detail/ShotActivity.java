package com.tangyang.fribbble.view.shot_detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
                        .add(R.id.fragment_container, new ShotFragment())
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
}
