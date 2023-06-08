package com.hk.ogrencievi.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

public class SFPDialog extends Dialog {
    public SFPDialog(@NonNull Context context, String drawable) {
        super(context);
        setContentView(R.layout.show_fullscreen_picture_dialog);


        ImageView imageView = findViewById(R.id.sfpd_image);
        Picasso.get().load(Uri.parse(drawable)).into(imageView);

    }
}
