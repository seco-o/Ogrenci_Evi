package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hk.ogrencievi.Adapter.FavoritesAdapter;
import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Model.EvIlani;
import com.hk.ogrencievi.Model.Product;
import com.hk.ogrencievi.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class IlanlarimActivity extends AppCompatActivity {
    private Activity mActivity;
    private ArrayList<Product> products;
    private FavoritesAdapter adapter;
    private FirebaseFirestore mFirestore;
    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilanlarim);

        bindIDs();
    }

    private void bindIDs() {
        mActivity = this;
        mFirestore = FirebaseFirestore.getInstance();
        products = new ArrayList<>();
        backImage = findViewById(R.id.ilanlarim_back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.ilanlarim_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new FavoritesAdapter(products, new FavoritesAdapter.FAListener() {
            @Override
            public void onCardClicked(int position) {
                Intent intent;
                if(products.get(position).getFiyat() == -1){
                    intent = new Intent(mActivity, EvIlaniOlusturActivity.class);
                    intent.putExtra("hasMyIlan", true);
                }else{
                    intent = new Intent(mActivity, IlanOlustur.class);
                    intent.putExtra("id", products.get(position).getId());
                }
                startActivity(intent);
            }

            @Override
            public void onCardLongClicked(int position) {
                deleteAction(position);
            }
        });
        recyclerView.setAdapter(adapter);

        getData();
        getEvIlani();
    }

    private void getEvIlani() {
        mFirestore.collection("evs")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value != null){
                            Map<String, Object> data = value.getData();
                            if (data != null) {
                                String id = (String) data.get("uuid");
                                String ad = (String) data.get("ad");
                                String soyad = (String) data.get("soyad");
                                String oneSignal = (String) data.get("oneSignal");
                                String email = (String) data.get("email");
                                String okul = (String) data.get("okul");
                                String pp = (String) data.get("pp");
                                String telNo = (String) data.get("telNo");

                                Product product = new Product(id, "", email, -1,
                                        pp, "", okul, "Ev İlanım", new ArrayList<>());

                                products.add(product);


                            }
                        }

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirestore.collection("evs")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data == null){
                            if(!products.isEmpty()){
                                Product p = products.get(products.size()-1);
                                if(p.getFiyat() == -1){
                                    products.remove(products.size() - 1);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });


    }

    private void deleteAction(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Sil");
        builder.setMessage("Bu ilan silinsin mi?");
        builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
                if(products.get(position).getFiyat() != -1){
                    mFirestore.collection("products").document(products.get(position).getId())
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pd.dismiss();
                                    Toast.makeText(mActivity, "Silindi", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    PublicHelper.showError(mActivity, e.getLocalizedMessage());
                                }
                            });
                }else{
                    mFirestore.collection("evs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pd.dismiss();
                                    Toast.makeText(mActivity, "Silindi", Toast.LENGTH_SHORT).show();
                                    onResume();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    PublicHelper.showError(mActivity, e.getLocalizedMessage());
                                }
                            });
                }

            }
        }).show();
    }

    private void getData() {
        mFirestore.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            products.clear();
                            for (DocumentSnapshot ds : value.getDocuments()) {
                                Map<String, Object> data = ds.getData();
                                if (data != null) {
                                    String id = ds.getId();
                                    String aciklama = (String) data.get("aciklama");
                                    String email = (String) data.get("email");
                                    long fiyat = (long) data.get("fiyat");
                                    String image = (String) data.get("image");
                                    String kategori = (String) data.get("kategori");
                                    String okul = (String) data.get("okul");
                                    String title = (String) data.get("title");

                                    String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                    ArrayList<String> images = (ArrayList<String>) data.get("photos");
                                    if (images == null) {
                                        images = new ArrayList<>();
                                    }
                                    if (myEmail.equals(email)) {
                                        Product p = new Product(id, aciklama, email,
                                                Integer.parseInt(String.valueOf(fiyat)), image,
                                                kategori, okul, title, images);
                                        products.add(p);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(mActivity, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });

    }


}