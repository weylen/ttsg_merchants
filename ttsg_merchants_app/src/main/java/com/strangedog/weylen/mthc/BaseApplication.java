package com.strangedog.weylen.mthc;

import android.app.Application;

import com.rey.material.app.ThemeManager;

/**
 * Created by Administrator on 2016-06-17.
 */
public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        ThemeManager.init(this, 2, 0, null);
    }
}
