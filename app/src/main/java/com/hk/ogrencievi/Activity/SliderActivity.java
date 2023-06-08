package com.hk.ogrencievi.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.hk.ogrencievi.Adapter.SliderAdapterSlider;
import com.hk.ogrencievi.Helper.IntentHelper;
import com.hk.ogrencievi.Helper.SPHelper;
import com.hk.ogrencievi.R;

public class SliderActivity extends AppCompatActivity {
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slider);

        bindIDs();
        checkIsFirst();



    }

    private void checkIsFirst() {
        if(!SPHelper.getBoolean(mActivity,SPHelper.NOTFIRST)){
            SPHelper.setBoolean(mActivity,SPHelper.NOTFIRST,true);
        }else{
            IntentHelper.goAndDestroy(mActivity,LoginActivity.class);
        }
    }

    private void bindIDs() {
        mActivity = this;
        ViewPager viewPager = findViewById(R.id.pager);
        int[] layouts = new int[]{R.layout.slider1, R.layout.slider2, R.layout.slider3, R.layout.slider4};
        SliderAdapterSlider sliderAdapterSlider = new SliderAdapterSlider(this, layouts, new SliderAdapterSlider.PagerAdapterListener() {
            @Override
            public void onLoginClicked() {
                IntentHelper.go(mActivity, LoginActivity.class);
            }
        });
        viewPager.setAdapter(sliderAdapterSlider);

        findViewById(R.id.slider_giris)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.go(mActivity,LoginActivity.class);
                    }
                });
    }


}
