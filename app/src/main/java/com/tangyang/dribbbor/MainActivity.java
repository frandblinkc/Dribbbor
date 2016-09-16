package com.tangyang.dribbbor;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.tangyang.dribbbor.view.shot_list.ShotListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.drawer) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ShotListFragment.newInstance())
                    .commit();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_item_home:
                        Toast.makeText(MainActivity.this, "home clicked", Toast.LENGTH_LONG);
                        break;

                    case R.id.drawer_item_likes:
                        Toast.makeText(MainActivity.this, "likes clicked", Toast.LENGTH_LONG);
                        break;

                    case R.id.drawer_item_buckets:
                        Toast.makeText(MainActivity.this, "buckets clicked", Toast.LENGTH_LONG);
                        break;
                }
                return false;
            }
        });
    }
}
