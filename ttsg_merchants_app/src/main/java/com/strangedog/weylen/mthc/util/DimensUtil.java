package com.strangedog.weylen.mthc.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2016-07-04.
 */
public class DimensUtil {

    public static int px2dp(Context context, float px){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (px / displayMetrics.density + 0.5);
    }

    public static int dp2px(Context context, float dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.density * dp + 0.5);
    }
}
