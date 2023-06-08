package com.hk.ogrencievi.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class IntentHelper {
    public static void go(Context context,Class clas){
        Intent intent = new Intent(context,clas);
        context.startActivity(intent);
    }
    public static void goAndDestroy(Activity activity, Class clas){
        Intent intent = new Intent(activity,clas);
        activity.startActivity(intent);
        activity.finishAffinity();
    }
}
