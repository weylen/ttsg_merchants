package com.strangedog.weylen.mthc.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by weylen on 2016-11-03.
 */
public class PhonePrefs {
    private static final String DB_NAME = "phone_db";

    public static final void savePhone(Context context, String phone){
        SharedPreferences preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString("phone", phone).commit();
    }

    public static final String getPhone(Context context){
        SharedPreferences preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        return preferences.getString("phone", "");
    }
}
