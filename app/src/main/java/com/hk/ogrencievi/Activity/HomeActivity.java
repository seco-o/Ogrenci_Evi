package com.hk.ogrencievi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import com.hk.ogrencievi.Helper.IntentHelper;
import com.hk.ogrencievi.Helper.SPHelper;
import com.hk.ogrencievi.R;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.achome_esyalaraulas)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPHelper.setBoolean(HomeActivity.this,SPHelper.IS_EV,false);
                        IntentHelper.goAndDestroy(HomeActivity.this, MainActivity.class);
                    }
                });
        findViewById(R.id.achome_evarkadasi)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPHelper.setBoolean(HomeActivity.this,SPHelper.IS_EV,true);
                        IntentHelper.goAndDestroy(HomeActivity.this, MainActivity.class);
                    }
                });


    }










}