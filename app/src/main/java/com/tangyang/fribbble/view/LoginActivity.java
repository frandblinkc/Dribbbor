package com.tangyang.fribbble.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tangyang.fribbble.R;
import com.tangyang.fribbble.dribbble.Auth.Auth;
import com.tangyang.fribbble.dribbble.Auth.AuthActivity;
import com.tangyang.fribbble.dribbble.Dribbble;
import com.tangyang.fribbble.dribbble.DribbbleException;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tangy on 9/18/2016.
 */
public class LoginActivity extends AppCompatActivity{
    @BindView(R.id.activity_login_btn) TextView loginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Dribbble.init(LoginActivity.this);

        if(Dribbble.isLoggedIn()) {
            Log.d("frandblinkc", "already logged in!");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Auth.openAuthActivity(LoginActivity.this);
                }
            });
        }

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Auth.REQ_CODE && resultCode == RESULT_OK) {
            final String code = data.getStringExtra(AuthActivity.KEY_CODE);
            Log.d("frandblinkc", "code=" + code);
//            Toast.makeText(LoginActivity.this, "code=" + code, Toast.LENGTH_LONG).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("frandblinkc", "in the new thread!");
                        String accessToken = Auth.fetchAccessToken(code);
                        Dribbble.login(LoginActivity.this, accessToken);
                        Log.d("frandblinkc", "access token is" + accessToken);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException | DribbbleException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }




    }
}
