package com.hk.ogrencievi.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hk.ogrencievi.Adapter.SigninAdapter;
import com.hk.ogrencievi.R;

public class SignInActivity extends AppCompatActivity {
    private Activity mActivity;
    SigninAdapter signinAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signin);

        bindIDs();

    }

    private void bindIDs() {
        mActivity = this;
        ViewPager viewPager = findViewById(R.id.signin_pager);
        int[] layouts = new int[]{R.layout.signup_slider_1, R.layout.signup_slider_2, R.layout.signup_slider_3, R.layout.signup_slider_4};
         signinAdapter = new SigninAdapter(mActivity, layouts, new SigninAdapter.SigninAdapterListener() {
            @Override
            public void onPageChange(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        viewPager.setAdapter(signinAdapter);



        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(signinAdapter != null){
            signinAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }





}