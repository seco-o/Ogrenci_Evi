package com.hk.ogrencievi.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hk.ogrencievi.Activity.EvIlaniDetay;
import com.hk.ogrencievi.Activity.EvIlaniOlusturActivity;
import com.hk.ogrencievi.Activity.HomeActivity;
import com.hk.ogrencievi.Activity.IlanDetayActivity;
import com.hk.ogrencievi.Activity.IlanOlustur;
import com.hk.ogrencievi.Activity.MessageActivity;
import com.hk.ogrencievi.Activity.TumunuGorActivity;
import com.hk.ogrencievi.Adapter.EvIlaniAdapter;
import com.hk.ogrencievi.Adapter.ProductAdapter;
import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.Helper.IntentHelper;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Helper.SPHelper;
import com.hk.ogrencievi.Model.EvIlani;
import com.hk.ogrencievi.Model.ProdUser;
import com.hk.ogrencievi.Model.Product;
import com.hk.ogrencievi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private ArrayList<Product> products, geciciProducts;
    private ArrayList<EvIlani> evIlanis, geciciEvilanis;
    private ProductAdapter adapter;
    private EvIlaniAdapter evIlaniAdapter;
    private FirebaseFirestore mFirestore;
    private LinearLayout elektronikLinear, mobilyaLinear,
            giyimLinear, egitimLinear;
    private TextView elektronikText, mobilyaText, giyimText,
            egitimText;
    private NestedScrollView elektronikNested, mobilyaNested, giyimNested,
            egitimNested;
    private EditText araEdit;
    private String globalKategori;
    private String globalOkul;
    private TextView homeText;
    private boolean isEv = false;
    private boolean hasMyIlan = false;
    private TextView ilanOlusturText;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container,
                false);
        isEv = SPHelper.getBoolean(getActivity(), SPHelper.IS_EV);
        products = new ArrayList<>();
        geciciProducts = new ArrayList<>();
        evIlanis = new ArrayList<>();
        geciciEvilanis = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        homeText = view.findViewById(R.id.homefragment_title);
        if (isEv) {
            homeText.setText("Ev Arkadaşı Bul");
        }
        elektronikLinear = view.findViewById(R.id.homefragment_elektronik);
        mobilyaLinear = view.findViewById(R.id.homefragment_mobilya);
        giyimLinear = view.findViewById(R.id.homefragment_giyim);
        egitimLinear = view.findViewById(R.id.homefragment_egitim);

        elektronikText = view.findViewById(R.id.homefragment_elektroniktext);
        mobilyaText = view.findViewById(R.id.homefragment_mobilyatext);
        giyimText = view.findViewById(R.id.homefragment_giyimtext);
        egitimText = view.findViewById(R.id.homefragment_egitimtext);

        elektronikNested = view.findViewById(R.id.homefragment_elektroniknested);
        mobilyaNested = view.findViewById(R.id.homefragment_mobilyanested);
        giyimNested = view.findViewById(R.id.homefragment_giyimnested);
        egitimNested = view.findViewById(R.id.homefragment_egitimnested);

        araEdit = view.findViewById(R.id.homefragment_araedit);

        araEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    if (isEv) {
                        evFiltrele("");
                    } else {
                        filtrele("");
                    }
                }
            }
        });
        araEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Handler handler = new Handler();
                    ProgressGifDialog pd = PublicHelper.showProgress(getActivity());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            if (isEv) {
                                evFiltrele(araEdit.getText().toString().trim().toLowerCase());
                            } else {
                                filtrele(araEdit.getText().toString().trim().toLowerCase());
                            }
                        }
                    }, 1250);
                    return true;
                }
                return false;
            }
        });

        ilanOlusturText = view.findViewById(R.id.homefragment_ilanolustur);
        ilanOlusturText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEv) {
                    Intent intent = new Intent(getActivity(), EvIlaniOlusturActivity.class);
                    intent.putExtra("hasMyIlan", hasMyIlan);
                    startActivity(intent);
                } else {
                    IntentHelper.go(getActivity(), IlanOlustur.class);
                }
            }
        });


        RecyclerView recyclerView = view.findViewById(R.id.homefragment_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new ProductAdapter(geciciProducts, new ProductAdapter.PAListener() {
            @Override
            public void onCarcClicked(int position) {
                if (geciciProducts.get(position).getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    Intent intent = new Intent(getActivity(), IlanOlustur.class);
                    intent.putExtra("id", products.get(position).getId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), IlanDetayActivity.class);
                    intent.putExtra("id", geciciProducts.get(position).getId());
                    startActivity(intent);
                }
            }
        });
        evIlaniAdapter = new EvIlaniAdapter(geciciEvilanis, new EvIlaniAdapter.EIAListener() {
            @Override
            public void onCardClicked(int position) {
                if (geciciEvilanis.get(position).getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    Intent intent = new Intent(getActivity(), EvIlaniOlusturActivity.class);
                    intent.putExtra("hasMyIlan", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), EvIlaniDetay.class);
                    intent.putExtra("email", geciciEvilanis.get(position).getEmail());
                    startActivity(intent);
                }
            }
        });


        elektronikLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllCategory(0);
            }
        });
        mobilyaLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllCategory(1);
            }
        });
        giyimLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllCategory(2);
            }
        });
        egitimLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllCategory(3);
            }
        });

        view.findViewById(R.id.home_fragment_tumunugor)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEv) {
                            if (globalKategori != null && globalOkul != null) {
                                Intent intent = new Intent(getActivity(), TumunuGorActivity.class);
                                intent.putExtra("okul", globalOkul);
                                intent.putExtra("kategori", globalKategori);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(getActivity(), TumunuGorActivity.class);
                            intent.putExtra("isEv", true);
                            startActivity(intent);
                        }

                    }
                });


        controllCategory(0);

        if (isEv) {
            recyclerView.setAdapter(evIlaniAdapter);
            view.findViewById(R.id.homefragment_kategorilinear)
                    .setVisibility(View.GONE);
            getEvArkadaslari();
        } else {
            recyclerView.setAdapter(adapter);
            getSchool(0);
        }

        return view;
    }


    private void getEvArkadaslari() {
        mFirestore.collection("evs").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                evIlanis.clear();
                geciciEvilanis.clear();
                boolean thereIsMyIlan = false;
                String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                assert value != null;
                for (DocumentSnapshot ds : value.getDocuments()) {
                    Map<String, Object> data = ds.getData();
                    if (data != null) {
                        String id = (String) data.get("uuid");
                        String ad = (String) data.get("ad");
                        String soyad = (String) data.get("soyad");
                        String oneSignal = (String) data.get("oneSignal");
                        String email = (String) data.get("email");
                        String okul = (String) data.get("okul");
                        String pp = (String) data.get("pp");
                        String telNo = (String) data.get("telNo");

                        if (evIlanis.size() < 6) {
                            EvIlani evIlani = new EvIlani(ad, email, okul,
                                    oneSignal, pp, soyad, telNo, id);

                            evIlanis.add(evIlani);
                        }
                        if(email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            thereIsMyIlan = true;
                        }


                    }
                }
                hasMyIlan = thereIsMyIlan;
                setIlanOlusturState();
                evFiltrele("");
            }
        });
    }

    private void setIlanOlusturState() {
        if (hasMyIlan) {
            ilanOlusturText.setText("Ev ilanımı kaldır");
        } else {
            ilanOlusturText.setText("İlan oluştur");
        }
    }

    private void evFiltrele(String s) {
        geciciEvilanis.clear();
        if (!s.isEmpty()) {
            ArrayList<String> okuls = new ArrayList<>();
            for (int i = 0; i < evIlanis.size(); i++) {
                EvIlani e = evIlanis.get(i);
                okuls.add(e.getOkul().toLowerCase());
            }
            ArrayList<Integer> indexs = new ArrayList<>();
            for (int i = 0; i < okuls.size(); i++) {
                if (okuls.get(i).contains(s.toLowerCase())) {
                    indexs.add(i);
                }
            }
            for (int i = 0; i < indexs.size(); i++) {
                geciciEvilanis.add(evIlanis.get(indexs.get(i)));
            }

            homeText.setText(geciciEvilanis.size() + " sonuç bulundu");
        } else {
            geciciEvilanis.addAll(evIlanis);
            if (isEv) {
                homeText.setText("Ev Arkadaşını Bul");
            }
        }
        evIlaniAdapter.notifyDataSetChanged();
    }

    private void getSchool(int position) {
        mFirestore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            String okul = (String) data.get("okulName");
                            globalOkul = okul;
                            getData(position, okul);
                        } else {
                            PublicHelper.showError(getActivity(), "Beklenmeyen hata");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PublicHelper.showError(getActivity(), e.getLocalizedMessage());
                    }
                });
    }

    private void controllCategory(int position) {

        if (position == 0) {
            elektronikText.setTextColor(getActivity().getColor(R.color.color1));
            elektronikNested.setVisibility(View.VISIBLE);
        } else if (position == 1) {
            mobilyaText.setTextColor(getActivity().getColor(R.color.color1));
            mobilyaNested.setVisibility(View.VISIBLE);
        } else if (position == 2) {
            giyimText.setTextColor(getActivity().getColor(R.color.color1));
            giyimNested.setVisibility(View.VISIBLE);
        } else if (position == 3) {
            egitimText.setTextColor(getActivity().getColor(R.color.color1));
            egitimNested.setVisibility(View.VISIBLE);
        }
        allToNormal(position);
        getSchool(position);
    }

    private void allToNormal(int position) {
        if (position != 0) {
            elektronikText.setTextColor(Color.WHITE);
            elektronikNested.setVisibility(View.INVISIBLE);
        }
        if (position != 1) {
            mobilyaText.setTextColor(Color.WHITE);
            mobilyaNested.setVisibility(View.INVISIBLE);
        }
        if (position != 2) {
            giyimText.setTextColor(Color.WHITE);
            giyimNested.setVisibility(View.INVISIBLE);
        }
        if (position != 3) {
            egitimText.setTextColor(Color.WHITE);
            egitimNested.setVisibility(View.INVISIBLE);
        }
    }

    private void getData(int position, String okulName) {
        if (position == 0) {
            globalKategori = "elektronik";
        } else if (position == 1) {
            globalKategori = "mobilya";
        } else if (position == 2) {
            globalKategori = "giyim";
        } else if (position == 3) {
            globalKategori = "eğitim";
        }

        mFirestore.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (value != null) {
                                products.clear();
                                for (DocumentSnapshot ds : value.getDocuments()) {
                                    Map<String, Object> data = ds.getData();
                                    if (data != null) {
                                        String id = ds.getId();
                                        long fiyat = (long) data.get("fiyat");
                                        String aciklama = (String) data.get("aciklama");
                                        String email = (String) data.get("email");
                                        String image = (String) data.get("image");
                                        String okul = (String) data.get("okul");
                                        String title = (String) data.get("title");
                                        String kategori = (String) data.get("kategori");
                                        ArrayList<String> images = (ArrayList<String>) data.get("photos");
                                        if (images == null) {
                                            images = new ArrayList<>();
                                        }


                                        if (products.size() < 6
                                        ) {
                                            Product product = new Product(id, aciklama, email,
                                                    Integer.parseInt(String.valueOf(fiyat)), image,
                                                    kategori, okul, title, images);
                                            if (position == 0 && kategori.equals("elektronik")) {
                                                products.add(product);
                                            } else if (position == 1 && kategori.equals("mobilya")) {
                                                products.add(product);
                                            } else if (position == 2 && kategori.equals("giyim")) {
                                                products.add(product);
                                            } else if (position == 3 && kategori.equals("eğitim")) {
                                                products.add(product);
                                            }
                                        }

                                    }
                                    filtrele("");
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        } else {
                            PublicHelper.showError(getActivity(), error.getLocalizedMessage());

                        }
                    }
                });


    }

    private void filtrele(String s) {
        geciciProducts.clear();
        if (!s.isEmpty()) {
            ArrayList<String> titles = new ArrayList<>();
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                titles.add(p.getTitle().toLowerCase());
            }
            ArrayList<Integer> indexs = new ArrayList<>();
            for (int i = 0; i < titles.size(); i++) {
                if (titles.get(i).contains(s.toLowerCase())) {
                    indexs.add(i);
                }
            }
            for (int i = 0; i < indexs.size(); i++) {
                geciciProducts.add(products.get(indexs.get(i)));
            }
            homeText.setText(geciciProducts.size() + " sonuç bulundu");
        } else {
            geciciProducts.addAll(products);
            if (isEv) {
                homeText.setText("Ev Arkadaşı Bul");
            } else {
                homeText.setText("Aradığın Eşyayı Bul");
            }
        }

        adapter.notifyDataSetChanged();


    }


}
