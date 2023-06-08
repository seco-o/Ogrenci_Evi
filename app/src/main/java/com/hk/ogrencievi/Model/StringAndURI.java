package com.hk.ogrencievi.Model;

import android.net.Uri;

public class StringAndURI {
    private String image;
    private Uri uri;

    public StringAndURI(String image, Uri uri) {
        this.image = image;
        this.uri = uri;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
