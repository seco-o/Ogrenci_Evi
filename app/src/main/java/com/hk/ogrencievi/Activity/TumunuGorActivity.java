package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hk.ogrencievi.Adapter.EvIlaniAdapter;
import com.hk.ogrencievi.Adapter.ProductAdapter;
import com.hk.ogrencievi.Model.EvIlani;
import com.hk.ogrencievi.Model.Product;
import com.hk.ogrencievi.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class TumunuGorActivity extends AppCompatActivity {
    private Activity mActivity;
    private ArrayList<Product> products;
    private String kategori;
    private String okulum;
    private ProductAdapter adapter;
    private ImageView backImage;
    private TextView centerText;
    private FirebaseFirestore mFirestore;
    private boolean isEv;
    private EvIlaniAdapter evIlaniAdapter;
    private ArrayList<EvIlani> evIlanis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tumunu_gor);

        isEv = getIntent().getBooleanExtra("isEv",false);
        kategori = getIntent().getStringExtra("kategori");
        okulum = getIntent().getStringExtra("okul");




        bindIDs();


    }

    private void bindIDs() {
        mActivity = this;
        products = new ArrayList<>();
        evIlanis = new ArrayList<>();
        centerText = findViewById(R.id.atg_title);
        backImage = findViewById(R.id.atg_back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mFirestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.atg_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity,3));
        adapter = new ProductAdapter(products, new ProductAdapter.PAListener() {
            @Override
            public void onCarcClicked(int position) {
                Intent intent = new Intent(mActivity,IlanDetayActivity.class);
                intent.putExtra("id",products.get(position).getId());
                startActivity(intent);
            }
        });
        evIlaniAdapter = new EvIlaniAdapter(evIlanis, new EvIlaniAdapter.EIAListener() {
            @Override
            public void onCardClicked(int position) {
                Intent intent = new Intent(mActivity, MessageActivity.class);
                intent.putExtra("email", evIlanis.get(position).getEmail());
                startActivity(intent);
            }
        });

        if(!isEv){
            recyclerView.setAdapter(adapter);
            StringBuilder kategoriStringBuilder = new StringBuilder();
            char[] charArray = kategori.toCharArray();
            for(int i=0; i<charArray.length;i++){
                if(i == 0){
                    String firstString = String.valueOf(charArray[i]);
                    kategoriStringBuilder.append(firstString.toUpperCase());
                }else{
                    kategoriStringBuilder.append(charArray[i]);
                }
            }
            centerText.setText(kategoriStringBuilder.toString());
            getData();
        }else{
            recyclerView.setAdapter(evIlaniAdapter);
            centerText.setText("Ev Arkadaşı");
            getEvArkadaslari();
        }





    }

    private void getEvArkadaslari() {
        mFirestore.collection("evs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        evIlanis.clear();
                        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                        for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                            Map<String,Object> data = ds.getData();
                            if(data != null){
                                String ad = (String) data.get("ad");
                                String email = (String) data.get("email");
                                String okul = (String) data.get("okul");
                                String oneSignal = (String) data.get("oneSignal");
                                String pp = (String) data.get("pp");
                                String soyad = (String) data.get("soyad");
                                String telNo = (String) data.get("telNo");
                                String uuid = (String) data.get("uuid");

                                if(!Objects.requireNonNull(email).equals(myEmail)){
                                    EvIlani evIlani = new EvIlani(ad,email,okul,
                                            oneSignal,pp,soyad,telNo,uuid);
                                    evIlanis.add(evIlani);
                                }
                            }
                        }
                        evIlaniAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mActivity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void getData() {
        mFirestore.collection("products").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        products.clear();
                        for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                            Map<String,Object> data = ds.getData();
                            if(data != null){
                                String id = ds.getId();
                                String aciklama = (String) data.get("aciklama");
                                String email = (String) data.get("email");
                                long fiyatLong = (long) data.get("fiyat");
                                String image = (String) data.get("image");
                                String gelenKategori = (String) data.get("kategori");
                                String okul = (String) data.get("okul");
                                String title = (String) data.get("title");
                                String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                                ArrayList<String> images = (ArrayList<String>) data.get("photos");
                                if(images == null){
                                    images = new ArrayList<>();
                                }
                                if(!Objects.requireNonNull(myEmail).equals(email) &&
                                    kategori.equals(gelenKategori)
                                ){
                                    Product p = new Product(id,aciklama,email,
                                            Integer.parseInt(String.valueOf(fiyatLong)),image,
                                            kategori,okul,title,images);
                                    products.add(p);
                                }

                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mActivity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }







}