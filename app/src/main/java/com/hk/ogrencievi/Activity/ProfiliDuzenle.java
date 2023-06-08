package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Public.Constants;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfiliDuzenle extends AppCompatActivity {
    private Activity mActivity;
    private EditText adText,soyadText,
    emailText,telNoText,passwordText;
    private ImageView backImage,ppImage;
    private Button guncelleButton;
    private FirebaseFirestore mFirestore;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profili_duzenle);

        bindIDs();
    }

    private void bindIDs() {
        mActivity = this;
        adText = findViewById(R.id.pd_ad);
        soyadText = findViewById(R.id.pd_soyad);
        emailText = findViewById(R.id.pd_email);
        telNoText = findViewById(R.id.pd_tel);
        passwordText = findViewById(R.id.pd_password);
        backImage = findViewById(R.id.pd_back);
        ppImage = findViewById(R.id.pd_pp);
        guncelleButton = findViewById(R.id.pd_button);
        mFirestore = FirebaseFirestore.getInstance();
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ppImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(mActivity)
                        .start(Constants.PROFILE_EDIT_PP);
            }
        });

        guncelleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guncelleAction();
            }
        });

        getData();

    }

    private void guncelleAction() {
        String passwordString = passwordText.getText().toString().trim();
        if(!passwordString.isEmpty()){
            if(passwordString.length() >= 6){
                ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                Objects.requireNonNull(mAuth.getCurrentUser()).updatePassword(passwordString)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                guncelleOther(pd);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                PublicHelper.showError(mActivity,e.getLocalizedMessage());
                            }
                        });
            }else{
                Toast.makeText(mActivity, "Parola en az 6 karakter olmalıdır", Toast.LENGTH_SHORT).show();
            }

        }else{
            ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
            guncelleOther(pd);
        }
    }

    private void guncelleOther(ProgressGifDialog pd) {
        Map<String,Object> updateData = new HashMap<>();
        String ad = adText.getText().toString().trim();
        String soyad = soyadText.getText().toString().trim();
        String telNo = telNoText.getText().toString().trim();

        if(!ad.isEmpty()){
            updateData.put("ad",ad);
        }
        if(!soyad.isEmpty()){
            updateData.put("soyad",soyad);
        }
        if(!telNo.isEmpty()){
            updateData.put("telNo",telNo);
        }

        if(!updateData.isEmpty()){
            String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            mFirestore.collection("users").document(myEmail)
                    .update(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            pd.dismiss();
                            Toast.makeText(mActivity, "Başarılı", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            PublicHelper.showError(mActivity,e.getLocalizedMessage());
                        }
                    });
        }else{
            pd.dismiss();
            Toast.makeText(mActivity, "Başarılı", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getData() {
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        mFirestore.collection("users").document(Objects.requireNonNull(myEmail))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            String ad = (String) data.get("ad");
                            String soyad = (String) data.get("soyad");
                            String email = (String) data.get("email");
                            String pp = (String) data.get("pp");
                            String telNo = (String) data.get("telNo");
                            id = (String) data.get("uuid");

                            if(pp != null && !pp.isEmpty()){
                                Picasso.get().load(Uri.parse(pp)).into(ppImage);
                            }
                            adText.setText(ad);
                            soyadText.setText(soyad);
                            emailText.setText(email);
                            telNoText.setText(telNo);
                            pd.dismiss();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        PublicHelper.showError(mActivity,e.getLocalizedMessage());
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.PROFILE_EDIT_PP){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Uri uri = data.getData();
                    askAreYouSure(uri);
                }
            }
        }
    }

    private void askAreYouSure(Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Profil Fotoğrafı");
        builder.setMessage("Fotoğraf yüklensin mi?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yükle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yukleAction(uri);
            }
        }).setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void yukleAction(Uri uri) {
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference ref = mStorage.getReference("userfiles").child(id)
                .child("images").child("pp.jpg");

        UploadTask uploadTask = ref.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

                    mFirestore.collection("users").document(Objects.requireNonNull(myEmail))
                            .update("pp",downloadUri.toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    ppImage.setImageURI(uri);
                                    pd.dismiss();
                                    Toast.makeText(mActivity, "Başarılı", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    PublicHelper.showError(mActivity,e.getLocalizedMessage());
                                }
                            });
                } else {
                    pd.dismiss();
                    if (task.getException() != null) {
                        PublicHelper.showError(mActivity, task.getException().toString());
                    }
                }
            }
        });

    }








}