package com.tangyang.fribbble.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tangyang.fribbble.R;

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


}
