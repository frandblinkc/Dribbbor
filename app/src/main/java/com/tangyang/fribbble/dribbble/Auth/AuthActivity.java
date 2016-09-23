package com.tangyang.fribbble.dribbble.Auth;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.dribbble.Dribbble;

import java.io.IOException;

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

        progressBar.setMax(100);

        String url = Auth.REDIRECT_URI;

        testLoadToken();


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("frandblinkc", "shouldOverridingUrlLoading executed! + url=" + url);
                if (url.startsWith(Auth.REDIRECT_URI)) {
                    Uri uri = Uri.parse(url);
                    final String code = uri.getQueryParameter(KEY_CODE);
                    Log.d("frandblinkc", "code=" + code);
                    Toast.makeText(AuthActivity.this, "code=" + code, Toast.LENGTH_LONG).show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (code != null) {
                                    String accessToken = Auth.fetchAccessToken(code);
                                    Dribbble.storeAccessToken(AuthActivity.this, accessToken);
                                    Log.d("frandblinkc", "access token is" + accessToken);
                                }

                                //Toast.makeText(AuthActivity.this, "access_token=" + accessToken, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });




        webView.loadUrl(Auth.getAuthorizeUrl());


    }


    private void testLoadToken() {
        String token = Dribbble.loadAccessToken(AuthActivity.this);
        Log.d("frandblinkc", "successfully loaded token: " + token);
    }
}
