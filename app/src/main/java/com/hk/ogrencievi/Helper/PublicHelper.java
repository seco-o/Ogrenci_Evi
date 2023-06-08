package com.hk.ogrencievi.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.hk.ogrencievi.Dialog.ProgressGifDialog;
import com.hk.ogrencievi.R;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PublicHelper {
    public static ProgressGifDialog showProgress(Context context){
        ProgressGifDialog pgd = new ProgressGifDialog(context);
        pgd.show();
        return pgd;
    }

    public static void showDefAlert(Context context,String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.tamam), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public static void showError(Context context,String message){
        showDefAlert(context, context.getString(R.string.hata),message);
    }

    public static void sendPush(String message, ArrayList<String> aliciOneSignal) {
        String TAG = "sendPush";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("en",message);
            jsonObject.put("contents",contentObject);
            JSONArray jsonArray = new JSONArray();
            for(String s : aliciOneSignal){
                jsonArray.put(s);
            }
            jsonObject.put("include_player_ids",jsonArray);

            OneSignal.postNotification(jsonObject, new OneSignal.PostNotificationResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Log.d(TAG,"Suc: "+jsonObject.toString()); //Bildirim başarıyla gönderildi. Loga yazdırıyor.
                }
                @Override
                public void onFailure(JSONObject jsonObject) {
                    Log.d(TAG,"Fail: "+jsonObject.toString()); //Bildirim gönderirken hata. Loga yazdırıyor.
                }
            });
        }catch (Exception e){
            e.printStackTrace(); //Json hatası. Uygulama çökmemesi için burda exception yakalanıyor.
        }
    }













}
