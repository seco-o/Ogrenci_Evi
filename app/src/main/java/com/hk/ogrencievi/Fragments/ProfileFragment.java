package com.hk.ogrencievi.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hk.ogrencievi.Activity.IlanlarimActivity;
import com.hk.ogrencievi.Activity.LoginActivity;
import com.hk.ogrencievi.Activity.ProfiliDuzenle;
import com.hk.ogrencievi.Helper.IntentHelper;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private ImageView ppImage;
    private TextView nameText,schoolText;
    private CardView duzenle,ilanlarim,cikisYap;
    private FirebaseFirestore mFirestore;

    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment, container,
                false);

        ppImage = view.findViewById(R.id.profile_pp);
        nameText = view.findViewById(R.id.profile_name);
        schoolText = view.findViewById(R.id.profile_school);
        duzenle = view.findViewById(R.id.profile_duzenle);
        ilanlarim = view.findViewById(R.id.profile_ilanlarim);
        cikisYap = view.findViewById(R.id.profile_cikis);
        mFirestore = FirebaseFirestore.getInstance();

        getData();

        duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.go(getActivity(), ProfiliDuzenle.class);
            }
        });

        cikisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cikisAction();
            }
        });
        ilanlarim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.go(getActivity(), IlanlarimActivity.class);
            }
        });


        return view;
    }

    private void cikisAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Çıkış Yap");
        builder.setMessage("Çıkış yapmak istediğinizden emin misiniz?");
        builder.setPositiveButton("Çıkış Yap", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                IntentHelper.goAndDestroy(getActivity(), LoginActivity.class);
            }
        }).show();
    }

    private void getData() {
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        mFirestore.collection("users").document(Objects.requireNonNull(myEmail))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error == null){
                            Map<String,Object> data = value.getData();
                            if(data != null){
                                String ad = (String) data.get("ad");
                                String okulName = (String) data.get("okulName");
                                String pp = (String) data.get("pp");
                                String soyad = (String) data.get("soyad");

                                nameText.setText(ad+" "+soyad);
                                schoolText.setText(okulName);
                                Picasso.get().load(Uri.parse(pp)).into(ppImage);

                            }
                        }
                    }
                });

    }




}
