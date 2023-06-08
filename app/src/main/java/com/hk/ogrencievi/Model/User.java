package com.hk.ogrencievi.Model;

public class User {
    private final String ad;
    private final String soyad;
    private final String email;
    private final String telNo;
    private final String password;

    public User(String ad, String soyad, String email, String telNo, String password) {
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.telNo = telNo;
        this.password = password;
    }

    public String getAd() {
        return ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public String getEmail() {
        return email;
    }

    public String getTelNo() {
        return telNo;
    }

    public String getPassword() {
        return password;
    }
}
