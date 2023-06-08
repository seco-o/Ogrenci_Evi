package com.hk.ogrencievi.Model;

public class ProdUser {
    private final String ad;
    private final String documentUrl;
    private final String email;
    private final String okulName;
    private final boolean onay;
    private final String pp;
    private final String soyad;
    private final String telNo;
    private final String uuid;
    private final String oneSignal;

    public ProdUser(String ad, String documentUrl, String email, String okulName, boolean onay, String pp, String soyad, String telNo, String uuid, String oneSignal) {
        this.ad = ad;
        this.documentUrl = documentUrl;
        this.email = email;
        this.okulName = okulName;
        this.onay = onay;
        this.pp = pp;
        this.soyad = soyad;
        this.telNo = telNo;
        this.uuid = uuid;
        this.oneSignal = oneSignal;
    }

    public String getOneSignal() {
        return oneSignal;
    }

    public String getAd() {
        return ad;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getOkulName() {
        return okulName;
    }

    public boolean isOnay() {
        return onay;
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
