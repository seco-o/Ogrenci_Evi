package com.hk.ogrencievi.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SPHelper {
    public static final String NOTFIRST = "NotFirst.shared";
    public static final String IS_EV = "IsEv.shared";



    public static void setBoolean(Context context,String type,boolean value){
        getSp(context).edit().putBoolean(type,value).apply();
    }
    public static boolean getBoolean(Context context,String type){
        return getSp(context).getBoolean(type,false);
    }

    private static SharedPreferences getSp(Context context){
        return context.getSharedPreferences("com.hk.ogrencievi",Context.MODE_PRIVATE);
    }
}
