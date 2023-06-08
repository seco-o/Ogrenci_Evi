package com.hk.ogrencievi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hk.ogrencievi.R;

public class SliderAdapterSlider extends PagerAdapter {
    private final int[] layouts;
    private final LayoutInflater layoutInflater;
    private final PagerAdapterListener listener;
    public SliderAdapterSlider(Context context, int[] layouts, PagerAdapterListener listener){
        this.layouts = layouts;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(layouts[position],container,false);
        container.addView(view);
        if(position == 3){
            view.findViewById(R.id.sl4_katil)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onLoginClicked();
                        }
                    });
        }
        return view;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


    public interface PagerAdapterListener<T>{
        void onLoginClicked();
    }
}
