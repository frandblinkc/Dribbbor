package com.tangyang.fribbble.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.dribbble.Dribbble;
import com.tangyang.fribbble.view.bucket_list.BucketListFragment;
import com.tangyang.fribbble.view.shot_list.ShotListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.drawer) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);



        setUpDrawer();


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ShotListFragment.newInstance(
                                                        ShotListFragment.LIST_TYPE_POPULAR))
                    .commit();
        }


    }

    private void setUpDrawer() {

        drawerToggle = new ActionBarDrawerToggle(
                                        this,
                                        drawerLayout,
                                        R.string.open_drawer,
                                        R.string.close_drawer);

        drawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // display user info in navigation drawer header
        View headView = navigationView.getHeaderView(0);
        ((TextView) headView.findViewById(R.id.nav_header_user_name))
                .setText(Dribbble.getCurrentUser().name);

        // set the log out button function
        headView.findViewById(R.id.nav_header_logout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dribbble.logout(MainActivity.this);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.isChecked()) {
                    drawerLayout.closeDrawers();
                    return true;
                }

                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.drawer_item_home:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR);
                        setTitle(R.string.title_home);
                        break;

                    case R.id.drawer_item_likes:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR);
                        setTitle(R.string.title_likes);
                        break;

                    case R.id.drawer_item_buckets:
                        fragment = BucketListFragment.newInstance(false, null);
                        setTitle(R.string.title_buckets);
                        break;
                }

                drawerLayout.closeDrawers();

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // sync the toggle state after onRestoreInstanceState has occured
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // handle action bar items
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
