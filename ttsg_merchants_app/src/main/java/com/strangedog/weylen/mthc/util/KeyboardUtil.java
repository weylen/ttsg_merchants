package com.strangedog.weylen.mthc.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by weylen on 2016-07-24.
 */
public class KeyboardUtil {

    public static void hide(Context context, View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void show(Activity context, View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }
}
