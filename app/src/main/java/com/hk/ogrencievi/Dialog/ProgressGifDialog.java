package com.hk.ogrencievi.Dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hk.ogrencievi.R;

public class ProgressGifDialog extends Dialog {
    public ProgressGifDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.progress_gif);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
    }
}
