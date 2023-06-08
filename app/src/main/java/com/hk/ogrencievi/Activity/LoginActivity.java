package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hk.ogrencievi.Dialog.PasswordResetDialog;
import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.Helper.IntentHelper;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.R;

public class LoginActivity extends AppCompatActivity {
    private Activity mActivity;
    private Toolbar toolbar;
    private EditText emailEdit,passwordEdit;
    private TextView sifremiUnuttumText;
    private Button girisYapButton;
    private TextView hesapOlusturText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindIDs();
        checkUser();

    }

    private void checkUser() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            IntentHelper.goAndDestroy(mActivity,HomeActivity.class);
        }
    }

    private void bindIDs() {
        mActivity = this;
        toolbar = findViewById(R.id.login_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        emailEdit = findViewById(R.id.login_email);
        passwordEdit = findViewById(R.id.login_sifre);
        passwordEdit.setHint(getString(R.string.sifre));
        passwordEdit.setHintTextColor(getColor(R.color.hint));
        sifremiUnuttumText = findViewById(R.id.login_unuttum);
        girisYapButton = findViewById(R.id.login_giris);
        hesapOlusturText = findViewById(R.id.login_hesapolustur);
        hesapOlusturText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.go(mActivity,SignInActivity.class);
            }
        });

        sifremiUnuttumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordResetDialog prd = new PasswordResetDialog(mActivity, new PasswordResetDialog.PRDListener() {
                    @Override
                    public void onOkClicked(String email) {
                        passwordReset(email);
                    }
                });
                prd.show();
            }
        });


        passwordEdit.setTransformationMethod(new PasswordTransformationMethod());

        girisYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                if(!email.isEmpty() && !password.isEmpty()){
                    girisAction(email,password);
                }else{
                    Toast.makeText(mActivity, "Lütfen boşlukları doldurunuz", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void girisAction(String email,String password) {
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
                mFirestore.collection("users").document(email)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                boolean onay = documentSnapshot.getBoolean("onay");
                                if(onay){
                                    pd.dismiss();
                                    Toast.makeText(mActivity, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                                    IntentHelper.goAndDestroy(mActivity,HomeActivity.class);
                                }else{
                                    pd.dismiss();
                                    PublicHelper.showError(mActivity,"Hesabınız henüz onaylanmamış");
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                PublicHelper.showError(mActivity,e.getLocalizedMessage());
            }
        });
    }

    private void passwordReset(String email) {
        ProgressGifDialog pd = PublicHelper.showProgress(mActivity);
        FirebaseAuth mAuth =  FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                PublicHelper.showDefAlert(mActivity,"Gönderim Başarılı","Parola sıfırlama linkiniz email adresinize gönderildi");
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}