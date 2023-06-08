package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.R;

public class EvIlaniDetay extends AppCompatActivity {
    private Activity mActivity;
    private FirebaseFirestore mFirestore;
    private String email;
    private ImageView imageView,backImage;
    private TextView tanitimText,nameText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ev_ilani_detay);

        email = getIntent().getStringExtra("email");

        bindIDs();
        getData();
    }

    private void getData() {
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        mFirestore.collection("evs").document(email)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String ad = documentSnapshot.getString("ad");
                        String soyad = documentSnapshot.getString("soyad");

                        String pp = documentSnapshot.getString("pp");
                        String tanitim = documentSnapshot.getString("tanitim");

                        Glide.with(imageView).load(Uri.parse(pp)).into(imageView);
                        nameText.setText(ad+" "+soyad);
                        tanitimText.setText(tanitim);
                        pd.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        PublicHelper.showError(mActivity,e.getLocalizedMessage());
                    }
                });
    }

    private void bindIDs() {
        mActivity = this;
        mFirestore = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.evilanidetay_image);
        backImage = findViewById(R.id.evilanidetay_back);
        tanitimText = findViewById(R.id.evilanidetay_tanitimtext);
        nameText = findViewById(R.id.evilanidetay_name);
        button = findViewById(R.id.evilanidetay_mesajbutton);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MessageActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });






    }
}