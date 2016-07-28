package com.strangedog.weylen.mthc.util;

import android.util.Log;

/**
 * Created by Administrator on 2016-06-26.
 */
public class DebugUtil {

    public static final boolean DEBUG = true;
    private static final String TAG = "zhou";

    public static void d(String msg){
        if (DEBUG){
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg){
        if (DEBUG){
            Log.e(TAG, msg);
        }
    }
}
