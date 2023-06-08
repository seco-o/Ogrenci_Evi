package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.hk.ogrencievi.Adapter.PhotosAdapter;
import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Model.StringAndURI;
import com.hk.ogrencievi.Public.Constants;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class IlanOlustur extends AppCompatActivity {
    private ImageView image;
    private TextView imageText;
    private EditText baslikEdit, fiyatEdit,
            aciklamaEdit;
    private Spinner kategoriSpinner;
    private ArrayList<StringAndURI> images;

    private Button yukleButton;
    private Activity mActivity;
    private Uri imageUri;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private String id;
    private ArrayList<Uri> uris;
    private PhotosAdapter adapter;
    private LinearLayout otherImageLinear;
    private ArrayList<String> photosGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilan_olustur);
        id = getIntent().getStringExtra("id");
        bindIDs();
        checkImageState();
        if (id != null) {
            imageText.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            yukleButton.setText("Güncelle");
            getData();
        }
    }

    private void getData() {
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        mFirestore.collection("products").document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            String aciklama = (String) data.get("aciklama");
                            String email = (String) data.get("email");
                            long fiyatLong = (long) data.get("fiyat");
                            String imageString = (String) data.get("image");
                            String kategori = (String) data.get("kategori");
                            String okul = (String) data.get("okul");
                            String title = (String) data.get("title");
                            ArrayList<String> photos = (ArrayList<String>) data.get("photos");
                            if (photos == null) {
                                photos = new ArrayList<>();
                            }
                            photosGlobal = photos;

                            aciklamaEdit.setText(aciklama);
                            baslikEdit.setText(title);
                            int fiyat = Integer.parseInt(String.valueOf(fiyatLong));
                            fiyatEdit.setText(fiyat + " TL");
                            Picasso.get().load(Uri.parse(imageString)).into(image);
                            if (kategori.equals("elektronik")) {
                                kategoriSpinner.setSelection(0);
                            } else if (kategori.equals("mobilya")) {
                                kategoriSpinner.setSelection(1);
                            } else if (kategori.equals("giyim")) {
                                kategoriSpinner.setSelection(2);
                            } else if (kategori.equals("eğitim")) {
                                kategoriSpinner.setSelection(3);
                            }
                            pd.dismiss();
                            for (int i = 0; i < photos.size(); i++) {
                                images.add(new StringAndURI(photos.get(i), null));
                            }

                            adapter.notifyDataSetChanged();
                            setOtherImageAddState();

                        } else {
                            pd.dismiss();
                            Toast.makeText(mActivity, "Beklenmeyen hata", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(mActivity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void checkImageState() {
        if (imageUri != null) {
            image.setVisibility(View.VISIBLE);
            imageText.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.GONE);
            imageText.setVisibility(View.VISIBLE);
        }
    }

    private void bindIDs() {
        mActivity = this;
        uris = new ArrayList<>();
        images = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        image = findViewById(R.id.ilanolustur_image);
        imageText = findViewById(R.id.ilanolustur_imagetext);
        baslikEdit = findViewById(R.id.ilanolustur_baslik);
        fiyatEdit = findViewById(R.id.ilanolustur_fiyat);
        aciklamaEdit = findViewById(R.id.ilanolustur_aciklama);
        kategoriSpinner = findViewById(R.id.ilanolustur_kategorispinner);
        yukleButton = findViewById(R.id.ilanolustur_yuklebutton);
        otherImageLinear = findViewById(R.id.ilanolustur_otherimageadd);

        findViewById(R.id.ilanolustur_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, new String[]{"Elektronik", "Mobilya", "Giyim", "Eğitim"});
        kategoriSpinner.setAdapter(arrayAdapter);

        findViewById(R.id.ilanolustur_imagecard)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePicker.with(mActivity)
                                .start(Constants.UPLOAD_PRODUCT_IMAGE);
                    }
                });


        yukleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id == null) {
                    yukleAction();
                } else {
                    guncelleAction();
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.ilanolustur_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (id == null) {
            adapter = new PhotosAdapter(uris, new PhotosAdapter.PhotosAdapterListener() {
                @Override
                public void onItemClicked(int position) {
                    showPhotoDeleteDialog(position);
                }

                @Override
                public void onItemLongClicked(int position) {

                }
            });
        } else {
            adapter = new PhotosAdapter(new PhotosAdapter.PhotosAdapterListener() {
                @Override
                public void onItemClicked(int position) {
                    showPhotoDeleteDialog(position);
                }

                @Override
                public void onItemLongClicked(int position) {

                }
            }, images);
        }

        recyclerView.setAdapter(adapter);

        otherImageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(mActivity)
                        .start(Constants.ADD_PRODUCT_OTHERIMAGE_CODE);

            }
        });


    }

    private void guncelleAction() {
        String baslik = baslikEdit.getText().toString().trim();
        String fiyat = fiyatEdit.getText().toString().trim();
        String kategori = kategoriSpinner.getSelectedItem().toString().trim().toLowerCase();
        String aciklama = aciklamaEdit.getText().toString().trim();

        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getUri() != null) {
                uris.add(images.get(i).getUri());
            }
        }
        if (!baslik.isEmpty() && !fiyat.isEmpty() &&
                !kategori.isEmpty() && !aciklama.isEmpty()) {
            try {
                int fiyatInt = Integer.parseInt(fiyat.replace("TL", "").trim());
                Map<String, Object> data = new HashMap<>();
                ArrayList<String> photos = new ArrayList<>();
                for (int i = 0; i < images.size(); i++) {
                    if (!images.get(i).getImage().isEmpty()) {
                        photos.add(images.get(i).getImage());
                    }
                }
                data.put("title", baslik);
                data.put("fiyat", fiyatInt);
                data.put("kategori", kategori);
                data.put("aciklama", aciklama);
                data.put("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                data.put("photos",photos);
                ProgressGifDialog pd = PublicHelper.showProgress(mActivity);

                uploadToDatabase(data, pd);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "Lütfen geçerli bir fiyat giriniz", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mActivity, "Lütfen bütün alanları doldurunuz", Toast.LENGTH_SHORT).show();
        }

    }

    private void showPhotoDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Fotoğrafı Kaldır");
        builder.setMessage("Bu fotoğraf kaldırılsın mı?");
        builder.setPositiveButton("Kaldır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (id == null) {
                    uris.remove(position);
                    adapter.notifyDataSetChanged();
                    setOtherImageAddState();
                } else {
                    images.remove(position);
                    adapter.notifyDataSetChanged();
                    setOtherImageAddState();

                }
            }
        }).show();


    }

    private void setOtherImageAddState() {
        if (id == null) {
            if (uris.size() >= 5) {
                otherImageLinear.setVisibility(View.GONE);
            } else {
                otherImageLinear.setVisibility(View.VISIBLE);
            }
        } else {
            if (images.size() >= 5) {
                otherImageLinear.setVisibility(View.GONE);
            } else {
                otherImageLinear.setVisibility(View.VISIBLE);
            }
        }

    }


    private void yukleAction() {
        if (imageUri != null) {
            String baslik = baslikEdit.getText().toString().trim();
            String fiyat = fiyatEdit.getText().toString().trim();
            String kategori = kategoriSpinner.getSelectedItem().toString().trim().toLowerCase();
            String aciklama = aciklamaEdit.getText().toString().trim();
            if (!baslik.isEmpty() && !fiyat.isEmpty() &&
                    !kategori.isEmpty() && !aciklama.isEmpty()) {
                try {
                    int fiyatInt = Integer.parseInt(fiyat.replace("TL", "").trim());
                    Map<String, Object> data = new HashMap<>();
                    data.put("title", baslik);
                    data.put("fiyat", fiyatInt);
                    data.put("kategori", kategori);
                    data.put("aciklama", aciklama);
                    data.put("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                    ProgressGifDialog pd = PublicHelper.showProgress(mActivity);

                    uploadToDatabase(data, pd);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, "Lütfen geçerli bir fiyat giriniz", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mActivity, "Lütfen bütün alanları doldurunuz", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mActivity, "Lütfen bir fotoğraf seçiniz", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToDatabase(Map<String, Object> data, ProgressGifDialog pd) {
        if (id == null) {
            mFirestore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> gelenData = documentSnapshot.getData();
                            if (gelenData != null) {
                                String okul = (String) gelenData.get("okulName");
                                data.put("okul", okul);
                                if (uris.isEmpty()) {
                                    uploadPhoto(data, pd);
                                } else {
                                    uploadAll(pd, data);
                                }
                            } else {
                                pd.dismiss();
                                PublicHelper.showError(mActivity, "Beklenmeyen Hata");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            PublicHelper.showError(mActivity, e.getLocalizedMessage());
                        }
                    });
        } else {
            if (uris.isEmpty()) {
                //Diğer fotoğraflar yok
                Log.d(getClass().getName(),"Diğer fotolar yok");
                if(imageUri == null){
                    //Kapak fotoğrafı da yok
                    uploadToFirestore(data, pd);
                    Log.d(getClass().getName(),"Kapak fotoğrafı da yok");
                }else{
                    //Kapak fotoğrafı var
                    uploadPhoto(data, pd);
                    Log.d(getClass().getName(),"Kapak fotoğrafı var");

                }
            } else {
                //Diğer fotoğraflar var kapak fotosu olabilir
                uploadAllGuncelle(pd, data);
                Log.d(getClass().getName(),"Diğer fotolar var, kapak fotosu olabilir");

            }

        }


    }

    private void uploadAllGuncelle(ProgressGifDialog pd, Map<String, Object> data) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        StorageReference storageRef = storage.getReference(Objects.requireNonNull(myEmail));

        ArrayList<String> photos = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            if (!images.get(i).getImage().isEmpty()) {
                photos.add(images.get(i).getImage());
            }
        }
        for (int i = 0; i < uris.size(); i++) {
                String uuid = UUID.randomUUID().toString();
                final StorageReference ref = storageRef.child("myproducts/" + uuid + "mountains.jpg");
                UploadTask uploadTask = ref.putFile(uris.get(i));
                int finalI = i;
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            pd.dismiss();
                            PublicHelper.showError(mActivity, Objects.requireNonNull(task.getException()).getLocalizedMessage());
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
                            photos.add(downloadUri.toString());
                            if (finalI == uris.size() - 1) {
                                data.put("photos", photos);
                                if(imageUri != null){
                                    uploadPhoto(data, pd);
                                }else{
                                    uploadToFirestore(data,pd);
                                }
                            }
                        } else {
                            if (finalI == images.size() - 1) {
                                pd.dismiss();
                                PublicHelper.showError(mActivity, "Fotoğraf yüklenemedi");
                            }
                        }
                    }
                });

        }

    }


    private void uploadAll(ProgressGifDialog pd, Map<String, Object> data) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        StorageReference storageRef = storage.getReference(Objects.requireNonNull(myEmail));

        ArrayList<String> photos = new ArrayList<>();
        for (int i = 0; i < uris.size(); i++) {
            String uuid = UUID.randomUUID().toString();
            final StorageReference ref = storageRef.child("myproducts/" + uuid + "mountains.jpg");
            UploadTask uploadTask = ref.putFile(uris.get(i));
            int finalI = i;
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        pd.dismiss();
                        PublicHelper.showError(mActivity, Objects.requireNonNull(task.getException()).getLocalizedMessage());
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
                        photos.add(downloadUri.toString());
                        if (finalI == uris.size() - 1) {
                            data.put("photos", photos);
                            uploadPhoto(data, pd);
                        }
                    } else {
                        if (finalI == uris.size() - 1) {
                            pd.dismiss();
                            PublicHelper.showError(mActivity, "Fotoğraf yüklenemedi");
                        }
                    }
                }
            });
        }

    }


    private void uploadPhoto(Map<String, Object> data, ProgressGifDialog pd) {
        String uuid = UUID.randomUUID().toString();
        StorageReference ref = mStorage.getReference("productfiles").child((String) data.get("email"))
                .child(uuid + ".jpg");

        UploadTask uploadTask = ref.putFile(imageUri);
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
                    data.put("image", downloadUri.toString());
                    uploadToFirestore(data, pd);
                } else {
                    pd.dismiss();
                    if (task.getException() != null) {
                        PublicHelper.showError(mActivity, task.getException().toString());
                    }
                }
            }
        });


    }

    private void uploadToFirestore(Map<String, Object> data, ProgressGifDialog pd) {
        if (id == null) {
            mFirestore.collection("products").document()
                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            pd.dismiss();
                            Toast.makeText(mActivity, "İlan başarıyla yüklendi", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            PublicHelper.showError(mActivity, e.getLocalizedMessage());
                        }
                    });
        } else {
            mFirestore.collection("products").document(id)
                    .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            pd.dismiss();
                            Toast.makeText(mActivity, "Güncellendi", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.UPLOAD_PRODUCT_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    image.setImageURI(imageUri);
                    checkImageState();
                }

            }
        } else if (requestCode == Constants.ADD_PRODUCT_OTHERIMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (id == null) {
                        uris.add(data.getData());
                        adapter.notifyDataSetChanged();
                        setOtherImageAddState();
                    } else {
                        images.add(new StringAndURI("", data.getData()));
                        adapter.notifyDataSetChanged();
                        setOtherImageAddState();

                    }
                } else {
                    Toast.makeText(mActivity, "Geçersiz Veri", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}