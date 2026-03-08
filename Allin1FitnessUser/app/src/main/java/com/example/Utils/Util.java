package com.example.Utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

public class Util {

    public static void setSP(Context con, String uid) {
        SharedPreferences sp = con.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("uid", uid);
        editor.apply();
        editor.commit();
    }

    public static String getSP(Context con) {
        SharedPreferences sp = con.getSharedPreferences("User", Context.MODE_PRIVATE);
        return sp.getString("uid", "");
    }


}
