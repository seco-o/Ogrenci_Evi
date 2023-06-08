package com.hk.ogrencievi.Adapter;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hk.ogrencievi.Activity.LoginActivity;
import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.Helper.IntentHelper;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Model.User;
import com.hk.ogrencievi.Public.Constants;
import com.hk.ogrencievi.R;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SigninAdapter extends PagerAdapter {
    private final int[] layouts;
    private final LayoutInflater layoutInflater;
    private final ArrayList<String> okulNames;
    private final ArrayList<String> geciciOkulNames;
    private OkullarAdapter okullarAdapter;
    private EditText araEdit;
    private SigninAdapterListener listener;
    private User user;
    private Activity mActivity;
    private ImageView selectImage;
    private Uri userPhotoUri;
    private Uri belgeUri;
    private ImageView ppSelect;
    private String okulName;


    public SigninAdapter(Activity mActivity, int[] layouts, SigninAdapterListener listener) {
        this.mActivity = mActivity;
        this.layouts = layouts;
        this.layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = listener;
        okulNames = new ArrayList<>();
        geciciOkulNames = new ArrayList<>();

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);
        controlLayout(position, container);
        return view;
    }

    private void controlLayout(int position, ViewGroup view) {
        switch (position) {
            case 0:
                bindPageOneViews(view);
                pageOneActions();
                break;
            case 1:
                bindPageTwoViews(view);
                break;
            case 2:
                bindPageThreeViews(view);
                break;
            case 3:
                bindPageFourViews(view);
                break;
        }


    }

    private void bindPageFourViews(ViewGroup view) {
        view.findViewById(R.id.signup_slider_4_girisyap)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.goAndDestroy(mActivity, LoginActivity.class);
                    }
                });
    }

    private void bindPageThreeViews(ViewGroup view) {
        selectImage = view.findViewById(R.id.signup_slider_3_photo);
        Toolbar toolbar = view.findViewById(R.id.signup_slider_3_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPageChange(1);
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(mActivity)
                        .galleryOnly()
                        .start(Constants.SIGNIN_GALLERY);
            }
        });

        view.findViewById(R.id.signup_slider_3_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePicker.with(mActivity)
                                .cameraOnly()
                                .start(Constants.SIGNIN_GALLERY);
                    }
                });

        view.findViewById(R.id.signup_slider_3_tamamla)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        kayitAction(view);
                    }
                });

        CheckBox checkBox = view.findViewById(R.id.signup_slider_3_checkbox);
        checkBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String kvkkUri = "https://www.kvkk.gov.tr/Icerik/2033/Aydinlatma-Yukumlulugu-";
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(kvkkUri));
                mActivity.startActivity(intent);

                return true;
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(mActivity, "KVKK metni için uzun basınız", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void kayitAction(View view) {
        if (user != null) {
            CheckBox checkBox = view.findViewById(R.id.signup_slider_3_checkbox);

            if (belgeUri != null) {
                if(checkBox.isChecked() || true){ //TODO
                    ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
                    FirebaseStorage mStorage = FirebaseStorage.getInstance();
                    Bitmap bitmap = ((BitmapDrawable) selectImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] data = baos.toByteArray();

                    String uuid = UUID.randomUUID().toString();
                    StorageReference ref = mStorage.getReference("userfiles").child(uuid)
                            .child("document").child("document.jpg");

                    UploadTask uploadTask = ref.putBytes(data);
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
                                if(userPhotoUri != null){
                                    saveProfilePhoto(downloadUri.toString(),uuid,pd);
                                }else{
                                    saveToFirestore(downloadUri.toString(),uuid,"",pd);
                                }
                            } else {
                                pd.dismiss();
                                if (task.getException() != null) {
                                    PublicHelper.showError(mActivity, task.getException().toString());
                                }
                            }
                        }
                    });
                }else{
                    Toast.makeText(mActivity, "KVKK metnini onaylayın.", Toast.LENGTH_SHORT).show();
                }
                


            } else {
                Toast.makeText(mActivity, "Belge yüklenmemiş", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mActivity, "Beklenmeyen hata", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfilePhoto(String docDownloadUrk,String uuid,ProgressGifDialog pd) {
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference ref = mStorage.getReference("userfiles").child(uuid)
                .child("profilephoto").child("photo.jpg");

        Bitmap bitmap = ((BitmapDrawable) ppSelect.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();


        UploadTask uploadTask = ref.putBytes(data);
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
                    saveToFirestore(docDownloadUrk,uuid,downloadUri.toString(),pd);
                } else {
                    pd.dismiss();
                    if (task.getException() != null) {
                        PublicHelper.showError(mActivity, task.getException().toString());
                    }
                }
            }
        });
    }

    private void saveToFirestore(String docDownloadUrl,String uuid,String profilePhoto,ProgressGifDialog pd) {
        Map<String,Object> data = new HashMap<>();
        data.put("documentUrl",docDownloadUrl);
        data.put("uuid",uuid);
        data.put("pp",profilePhoto);
        data.put("ad",user.getAd());
        data.put("soyad",user.getSoyad());
        data.put("email",user.getEmail());
        data.put("telNo",user.getTelNo());
        data.put("okulName",okulName);
        data.put("onay",false);

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users").document(user.getEmail())
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        mAuth.signOut();
                                        pd.dismiss();
                                        listener.onPageChange(3);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        PublicHelper.showError(mActivity,e.getLocalizedMessage());
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        PublicHelper.showError(mActivity,e.getLocalizedMessage());
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.SIGNIN_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                selectImage.setImageURI(uri);
                belgeUri = uri;
            }
        } else if (requestCode == Constants.SIGININ_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                userPhotoUri = uri;
                ppSelect.setImageURI(uri);
            }
        }
    }


    private void bindPageTwoViews(ViewGroup view) {
        EditText adEdit = view.findViewById(R.id.ss2_ad);
        EditText soyadEdit = view.findViewById(R.id.ss2_soyad);
        EditText emailEdit = view.findViewById(R.id.ss2_email);
        EditText telNoEdit = view.findViewById(R.id.ss2_tel);
        TextInputEditText passwordEdit = view.findViewById(R.id.ss2_password);

        passwordEdit.setHint(mActivity.getString(R.string.sifre));
        passwordEdit.setHintTextColor(mActivity.getColor(R.color.hint));

        Toolbar toolbar = view.findViewById(R.id.signup_slider_2_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPageChange(0);
            }
        });

        ppSelect = view.findViewById(R.id.signup_slider_2_image);
        ppSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(mActivity)
                        .start(Constants.SIGININ_PROFILE);
            }
        });


        view.findViewById(R.id.ss2_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ad = adEdit.getText().toString().trim();
                String soyad = soyadEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String telNo = telNoEdit.getText().toString().trim();
                String password = Objects.requireNonNull(passwordEdit.getText()).toString().trim();
                
                if (!ad.isEmpty() && !soyad.isEmpty() &&
                        !email.isEmpty() && !telNo.isEmpty() && !password.isEmpty()) {
                    if(userPhotoUri != null){
                        user = new User(ad, soyad, email, telNo, password);
                        listener.onPageChange(2);
                    }else{
                        Toast.makeText(mActivity, "Lütfen bir fotoğraf seçiniz", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mActivity, "Lütfen bütün alanları doldurunuz", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void bindPageOneViews(ViewGroup view) {
        araEdit = view.findViewById(R.id.ss1_araedit);

        RecyclerView recyclerView = view.findViewById(R.id.ss1_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        okullarAdapter = new OkullarAdapter(geciciOkulNames, new OkullarAdapter.OkullarAdapterListener() {
            @Override
            public void theSchoolSelected(String schoolName) {
                araEdit.setText(schoolName);
            }
        });
        recyclerView.setAdapter(okullarAdapter);

        araEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrele();
            }
        });

        view.findViewById(R.id.ss1_ilerle)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String araString = araEdit.getText().toString().trim();
                        if (!araEdit.getText().toString().trim().isEmpty() &&
                                okulNames.contains(araEdit.getText().toString().trim())
                        ) {
                            okulName = araString;
                            listener.onPageChange(1);
                        } else {
                            Toast.makeText(mActivity, "Bir okul seçiniz", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        Toolbar toolbar = view.findViewById(R.id.signup_slider_1_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

    }

    private void pageOneActions() {
        getSchools();
    }

    private void getSchools() {
        okulNames.clear();
        geciciOkulNames.clear();
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("okullar").orderBy("name")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = ds.getData();
                            if (data != null) {
                                String name = (String) data.get("name");
                                okulNames.add(name);
                            }
                        }
                        pd.dismiss();
                        filtrele();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        PublicHelper.showError(mActivity, e.getLocalizedMessage());
                    }
                });

    }

    private void filtrele() {
        String girilenInput = araEdit.getText().toString().trim();
        geciciOkulNames.clear();
        for (int i = 0; i < okulNames.size(); i++) {
            String okulAdi = okulNames.get(i);
            if (okulAdi.contains(girilenInput)) {
                geciciOkulNames.add(okulAdi);
            }
        }
        okullarAdapter.notifyDataSetChanged();

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
        container.removeView((View) object);
    }

    public interface SigninAdapterListener<T> {
        void onPageChange(int position);
    }


}
