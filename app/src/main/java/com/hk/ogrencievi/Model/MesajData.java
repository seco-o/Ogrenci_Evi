package com.hk.ogrencievi.Model;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class MesajData {
    private final String userId;
    private final ArrayList<DataSnapshot> datas;

    public MesajData(String userId, ArrayList<DataSnapshot> datas) {
        this.userId = userId;
        this.datas = datas;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<DataSnapshot> getDatas() {
        return datas;
    }
}
