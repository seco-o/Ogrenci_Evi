package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hk.ogrencievi.Adapter.ProductAdapter;
import com.hk.ogrencievi.Fragments.FavoriteFragment;
import com.hk.ogrencievi.Fragments.HomeFragment;
import com.hk.ogrencievi.Fragments.MessageFragment;
import com.hk.ogrencievi.Fragments.ProfileFragment;
import com.hk.ogrencievi.Helper.IntentHelper;
import com.hk.ogrencievi.Model.Product;
import com.hk.ogrencievi.R;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Activity mActivity;
    private ImageView homeIcon,favoriteIcon,messageIcon,profileIcon;
    private TextView homeText,favoriteText,messageText,profileText;
    private LinearLayout homeLinear,favoriteLinear,messageLinear,profileLinear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindIDs();

        controllFragments(0);

        updateMyOneSignal();



    }

    private void updateMyOneSignal() {
        if(OneSignal.getDeviceState() != null){
            if(OneSignal.getDeviceState().getUserId() != null){
                String oneSignal = OneSignal.getDeviceState().getUserId();
                String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                mFirestore.collection("users").document(Objects.requireNonNull(myEmail))
                        .update("oneSignal",oneSignal);
            }
        }
    }

    private void bindIDs() {
        mActivity = this;
        homeIcon = findViewById(R.id.main_homeicon);
        favoriteIcon = findViewById(R.id.main_favoriteicon);
        messageIcon = findViewById(R.id.main_messageicon);
        profileIcon = findViewById(R.id.main_profileicon);

        homeText = findViewById(R.id.main_hometext);
        favoriteText = findViewById(R.id.main_favoritetext);
        messageText = findViewById(R.id.main_messagetext);
        profileText = findViewById(R.id.main_profiletext);

        homeLinear = findViewById(R.id.main_homelinear);
        favoriteLinear = findViewById(R.id.main_favoritelinear);
        messageLinear = findViewById(R.id.main_messagelinear);
        profileLinear = findViewById(R.id.main_profilelinear);

        findViewById(R.id.main_bottom_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.goAndDestroy(mActivity, HomeActivity.class);
                    }
                });

        homeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllFragments(0);
            }
        });
        favoriteLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllFragments(1);
            }
        });
        messageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllFragments(2);
            }
        });
        profileLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllFragments(3);
            }
        });
    }


    public void controllFragments(int position) {
        FragmentManager frgManager = getFragmentManager();
        switch(position){
            case 0:
                Fragment fragment = new HomeFragment();
                frgManager.beginTransaction().replace(R.id.main_content_frame, fragment)
                        .commit();

                homeText.setTextColor(getColor(R.color.color1));
                homeIcon.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.bottom_home_icon_selected));
                allToNormal(position);
                break;
            case 1:
                Fragment favoriteFragment = new FavoriteFragment();
                frgManager.beginTransaction().replace(R.id.main_content_frame, favoriteFragment)
                        .commit();

                favoriteText.setTextColor(getColor(R.color.color1));
                favoriteIcon.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.favorite_icon_selected));
                allToNormal(position);
                break;
            case 2:
                Fragment messageFragment = new MessageFragment();
                frgManager.beginTransaction().replace(R.id.main_content_frame, messageFragment)
                        .commit();
                messageText.setTextColor(getColor(R.color.color1));
                messageIcon.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.bottom_message_icon_selected));
                allToNormal(position);
                break;

            case 3:
                Fragment profilFragment = new ProfileFragment();
                frgManager.beginTransaction().replace(R.id.main_content_frame, profilFragment)
                        .commit();
                profileText.setTextColor(getColor(R.color.color1));
                profileIcon.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.bottom_user_icon_selected));
                allToNormal(position);
                break;
        }
    }

    private void allToNormal(int position) {
        if(position != 0){
            homeText.setTextColor(Color.WHITE);
            homeIcon.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.bottom_home_icon));
        }
        if(position != 1){
            favoriteText.setTextColor(Color.WHITE);
            favoriteIcon.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.favorite_icon));
        }
        if(position != 2){
            messageText.setTextColor(Color.WHITE);
            messageIcon.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.bottom_message_icon));
        }
        if(position != 3){
            profileText.setTextColor(Color.WHITE);
            profileIcon.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.bottom_user_icon));
        }
    }


}