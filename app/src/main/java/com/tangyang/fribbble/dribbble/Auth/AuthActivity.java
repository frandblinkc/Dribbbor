package com.tangyang.fribbble.dribbble.Auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.tangyang.fribbble.R;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by tangy on 9/19/2016.
 */
public class AuthActivity extends AppCompatActivity {









    public static final String KEY_URL = "url";
    public static final String KEY_CODE = "code";


    @BindView(R.id.progress_bar)ProgressBar progressBar;
    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.activitiy_login_title);

        String url = Auth.REDIRECT_URI;






        webView.loadUrl(Auth.getAuthorizeUrl());


    }
}
