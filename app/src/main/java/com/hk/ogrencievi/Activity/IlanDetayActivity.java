package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hk.ogrencievi.Dialog.SFPDialog;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Model.ProdUser;
import com.hk.ogrencievi.Model.Product;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class IlanDetayActivity extends AppCompatActivity {

    private ImageView backImage,userImage,
    favoriteImage;
    private TextView baslikText,fiyatText,aciklamaText,adSoyadText;
    private Button mesajAtButton,araButton;
    private String productId;
    private Activity mActivity;
    private FirebaseFirestore mFirestore;
    private boolean isFavorite = false;
    private String phoneNumber = "";
    private ProdUser u;
    private Product p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilan_detay);

        productId = getIntent().getStringExtra("id");
        bindIDs();
        getProduct();
        getFavorite();

    }

    private void getFavorite() {
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        mFirestore.collection("users").document(Objects.requireNonNull(myEmail))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            ArrayList<String> favorites = (ArrayList<String>) data.get("favorites");
                            if(favorites == null){
                                favorites = new ArrayList<>();
                            }
                            isFavorite = favorites.contains(productId);
                            setLocalFavorite();
                        }
                    }
                });
    }

    private void getProduct() {
        mFirestore.collection("products").document(productId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            String id = productId;
                            String aciklama = (String) data.get("aciklama");
                            String email = (String) data.get("email");
                            long fiyat = (long) data.get("fiyat");
                            String image = (String) data.get("image");
                            String kategori = (String) data.get("kategori");
                            String okul = (String) data.get("okul");
                            String title = (String) data.get("title");
                            ArrayList<String> images = (ArrayList<String>) data.get("photos");
                            if(images == null){
                                images = new ArrayList<>();
                            }

                            p = new Product(id,aciklama,email,Integer.parseInt(String.valueOf(fiyat)),
                                    image,kategori,okul,title,images);
                            setProductProperties();
                        }else{
                            PublicHelper.showError(mActivity,"Beklenmeyen hata");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PublicHelper.showError(mActivity,e.getLocalizedMessage());
                    }
                });
    }

    private void setProductProperties() {

        baslikText.setText(p.getTitle());
        fiyatText.setText("₺ "+p.getFiyat());
        aciklamaText.setText(p.getAciklama());
        getUser(p.getEmail());
    }

    private void getUser(String email) {
        mFirestore.collection("users").document(email)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            String ad = (String) data.get("ad");
                            String documentUrl = (String) data.get("documentUrl");
                            String okulName = (String) data.get("okulName");
                            boolean onay = (boolean) data.get("onay");
                            String pp = (String) data.get("pp");
                            String soyad = (String) data.get("soyad");
                            String telNo = (String) data.get("telNo");
                            String uuid = (String) data.get("uuid");
                            String oneSignal = (String) data.get("oneSignal");

                            u = new ProdUser(ad,documentUrl,email,okulName,onay,
                                    pp,soyad,telNo,uuid,oneSignal);
                            setUserProperties();
                        }else{
                            PublicHelper.showError(mActivity,"Beklenmeyen hata");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PublicHelper.showError(mActivity,e.getLocalizedMessage());
                    }
                });
    }

    private void setUserProperties() {
        Picasso.get().load(Uri.parse(u.getPp())).into(userImage);
        adSoyadText.setText(u.getAd()+" "+u.getSoyad());
        phoneNumber = u.getTelNo();

        setValues();

    }

    private void bindIDs() {
        mActivity = this;
        mFirestore = FirebaseFirestore.getInstance();
        backImage = findViewById(R.id.ilandetay_back);

        userImage = findViewById(R.id.ilandetay_userpp);
        favoriteImage = findViewById(R.id.ilandetay_favorite);

        adSoyadText = findViewById(R.id.ilandetay_person);
        baslikText = findViewById(R.id.ilandetay_baslik);
        fiyatText = findViewById(R.id.ilandetay_fiyat);
        aciklamaText = findViewById(R.id.ilandetay_aciklama);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mesajAtButton = findViewById(R.id.ilandetay_mesajat);
        araButton = findViewById(R.id.ilandetay_ara);

        araButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.d("Calling a Phone Number", "Call failed" + activityException);
                }
            }
        });
        mesajAtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(u != null){
                    Intent intent = new Intent(mActivity,MessageActivity.class);
                    intent.putExtra("email",u.getEmail());
                    startActivity(intent);
                }
            }
        });

        favoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavorite){
                    isFavorite = true;
                    setLocalFavorite();
                }else{
                    isFavorite = false;
                    setLocalFavorite();
                }
                setFavorite();
            }
        });



    }

    private void setLocalFavorite(){
        if(isFavorite){
            favoriteImage.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.baseline_favorite_24));
        }else{
            favoriteImage.setImageDrawable(AppCompatResources.getDrawable(mActivity,R.drawable.baseline_favorite_24_grey));
        }
    }

    private void setFavorite() {
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        mFirestore.collection("users").document(Objects.requireNonNull(myEmail))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            ArrayList<String> favorites = (ArrayList<String>) data.get("favorites");
                            if(favorites == null){
                                favorites = new ArrayList<>();
                            }
                            if(isFavorite){
                                favorites.add(productId);
                            }else{
                                favorites.remove(productId);
                            }
                            mFirestore.collection("users").document(myEmail)
                                    .update("favorites",favorites);
                        }
                    }
                });
    }

    private void setValues() {
        ArrayList<SlideModel> imageList = new ArrayList<>();
        SlideModel slideModel = new SlideModel(p.getImage(),"", ScaleTypes.CENTER_CROP);
        imageList.add(slideModel);
        for (int i = 0; i < p.getImages().size(); i++) {
            SlideModel slideModel2 = new SlideModel(p.getImages().get(i),"", ScaleTypes.CENTER_CROP);
            imageList.add(slideModel2);

        }

        ImageSlider imageSlider = findViewById(R.id.ilandetay_slider);
        imageSlider.setImageList(imageList);

        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                SFPDialog dialog = new SFPDialog(mActivity,imageList.get(i).getImageUrl());
                dialog.show();
            }

            @Override
            public void doubleClick(int i) {

            }
        });





    }




}