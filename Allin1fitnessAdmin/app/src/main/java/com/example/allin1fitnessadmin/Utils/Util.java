package com.example.allin1fitnessadmin.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class Util {

    public static void setSP(Context con, String uid) {
        SharedPreferences sp = con.getSharedPreferences("Admin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("uid", uid);
        editor.apply();
        editor.commit();
    }

    public static String getSP(Context con) {
        SharedPreferences sp = con.getSharedPreferences("Admin", Context.MODE_PRIVATE);
        return sp.getString("uid", "");
    }

}
