package com.hk.ogrencievi.Model;

public class EvIlani {
    private final String ad;
    private final String email;
    private final String okul;
    private final String oneSignal;
    private final String pp;
    private final String soyad;
    private final String telNo;
    private final String uuid;

    public EvIlani(String ad, String email, String okul, String oneSignal, String pp, String soyad, String telNo, String uuid) {
        this.ad = ad;
        this.email = email;
        this.okul = okul;
        this.oneSignal = oneSignal;
        this.pp = pp;
        this.soyad = soyad;
        this.telNo = telNo;
        this.uuid = uuid;
    }

    public String getAd() {
        return ad;
    }

    public String getEmail() {
        return email;
    }

    public String getOkul() {
        return okul;
    }

    public String getOneSignal() {
        return oneSignal;
    }

    public String getPp() {
        return pp;
    }

    public String getSoyad() {
        return soyad;
    }

    public String getTelNo() {
        return telNo;
    }

    public String getUuid() {
        return uuid;
    }
}
