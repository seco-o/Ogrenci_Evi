package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Model.ProdUser;
import com.hk.ogrencievi.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EvIlaniOlusturActivity extends AppCompatActivity {
    private boolean hasMyIlan;
    private Activity mActivity;
    private FirebaseFirestore mFirestore;
    private Button button;
    private TextView textView;
    private EditText tanitimEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ev_ilani_olustur);

        hasMyIlan = getIntent().getBooleanExtra("hasMyIlan", false);

        bindIDs();
        if (hasMyIlan) {
            tanitimEdit.setEnabled(false);
        }
    }

    private void bindIDs() {
        mActivity = this;
        mFirestore = FirebaseFirestore.getInstance();
        button = findViewById(R.id.evilanolustur_button);
        textView = findViewById(R.id.evilanolustur_text);
        tanitimEdit = findViewById(R.id.evilanolustur_tanitimedit);

        setStates();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doIt();
            }
        });

        findViewById(R.id.evilanolustur_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
        if (hasMyIlan) {
            getData();
        }
    }

    private void getData() {
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        mFirestore.collection("evs").document(Objects.requireNonNull(myEmail))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            String tanitim = (String) data.get("tanitim");
                            tanitimEdit.setText(tanitim);
                        } else {
                            PublicHelper.showError(mActivity, "Beklenmeyen Hata");
                        }
                        pd.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        PublicHelper.showError(mActivity, e.getLocalizedMessage());
                    }
                });
    }

    private void setStates() {
        if (hasMyIlan) {
            button.setText("Ev İlanımı Kaldır");
            textView.setText("Ev İlanınız Mevcut");
        } else {
            button.setText("Ev İlanı Oluştur");
            textView.setText("Ev İlanınız Yok");
        }
    }


    private void doIt() {
        if (hasMyIlan) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Ev Arkadaşı İlanımı Kaldır");
            builder.setMessage("Ev arkadaşı ilanınızı kaldırmak istediğinizden emin misiniz?");
            builder.setPositiveButton("İlanımı Kaldır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteIlan();
                }
            }).show();
        } else {
            if (!tanitimEdit.getText().toString().trim().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Ev Arkadaşı İlanı Ver");
                builder.setMessage("Ev arkadaşı ilanı vermek istediğinizden emin misiniz?");
                builder.setPositiveButton("İlan Ver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getMyProfile();
                    }
                }).show();
            } else {
                Toast.makeText(mActivity, "Lütfen tanıtım yazısı giriniz", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void deleteIlan() {
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirestore.collection("evs").document(myEmail)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(mActivity, "İlan kaldırıldı", Toast.LENGTH_SHORT).show();
                        hasMyIlan = false;
                        setStates();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        PublicHelper.showError(mActivity, e.getLocalizedMessage());
                    }
                });
    }


    private void getMyProfile() {
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mFirestore.collection("users").document(myEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            String ad = (String) data.get("ad");
                            String documentUrl = (String) data.get("documentUrl");
                            String email = (String) data.get("email");
                            String okulName = (String) data.get("okulName");
                            boolean onay = (boolean) data.get("onay");
                            String oneSignal = (String) data.get("oneSignal");
                            String pp = (String) data.get("pp");
                            String soyad = (String) data.get("soyad");
                            String telNo = (String) data.get("telNo");
                            String uuid = (String) data.get("uuid");

                            ProdUser prodUser = new ProdUser(ad, documentUrl,
                                    email, okulName, onay, pp, soyad, telNo,
                                    uuid, oneSignal);
                            createData(prodUser, pd);
                        } else {
                            pd.dismiss();
                            PublicHelper.showError(mActivity, "Beklenmeyen hata");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        PublicHelper.showError(mActivity, e.getLocalizedMessage());
                    }
                });
    }

    private void createData(ProdUser prodUser, ProgressGifDialog pd) {
        Map<String, Object> data = new HashMap<>();
        data.put("ad", prodUser.getAd());
        data.put("soyad", prodUser.getSoyad());
        data.put("email", prodUser.getEmail());
        data.put("okul", prodUser.getOkulName());
        data.put("oneSignal", prodUser.getOneSignal());
        data.put("pp", prodUser.getPp());
        data.put("telNo", prodUser.getTelNo());
        data.put("uuid", prodUser.getUuid());
        data.put("tanitim", tanitimEdit.getText().toString().trim());

        mFirestore.collection("evs").document(prodUser.getEmail())
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(mActivity, "Başarıyla ilan verdiniz", Toast.LENGTH_SHORT).show();
                        hasMyIlan = true;
                        setStates();
                        finish();
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