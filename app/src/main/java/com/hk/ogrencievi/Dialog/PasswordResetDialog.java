package com.hk.ogrencievi.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hk.ogrencievi.R;

public class PasswordResetDialog extends Dialog {
    private final EditText editText;
    private final Button button;
    private final PRDListener listener;
    public PasswordResetDialog(@NonNull Context context,PRDListener listener) {
        super(context);
        setContentView(R.layout.email_reset_dialog);
        this.listener = listener;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        editText = findViewById(R.id.erd_edit);
        button = findViewById(R.id.erd_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editText.getText().toString().trim();
                if(!email.isEmpty()){
                    listener.onOkClicked(email);
                    dismiss();
                }else{
                    Toast.makeText(context, "Lütfen email adresi giriniz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public interface PRDListener<T>{
        void onOkClicked(String email);
    }
}
