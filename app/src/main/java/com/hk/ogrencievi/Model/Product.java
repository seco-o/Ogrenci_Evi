package com.hk.ogrencievi.Model;

import java.util.ArrayList;

public class Product {
   private final String id;
   private final String aciklama;
   private final String email;
   private final int fiyat;
   private final String image;
   private final String kategori;
   private final String okul;
   private final String title;
   private final ArrayList<String> images;

    public Product(String id, String aciklama, String email, int fiyat, String image, String kategori, String okul, String title, ArrayList<String> images) {
        this.id = id;
        this.aciklama = aciklama;
        this.email = email;
        this.fiyat = fiyat;
        this.image = image;
        this.kategori = kategori;
        this.okul = okul;
        this.title = title;
        this.images = images;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getId() {
        return id;
    }

    public String getAciklama() {
        return aciklama;
    }

    public String getEmail() {
        return email;
    }

    public int getFiyat() {
        return fiyat;
    }

    public String getImage() {
        return image;
    }

    public String getKategori() {
        return kategori;
    }

    public String getOkul() {
        return okul;
    }

    public String getTitle() {
        return title;
    }
}
