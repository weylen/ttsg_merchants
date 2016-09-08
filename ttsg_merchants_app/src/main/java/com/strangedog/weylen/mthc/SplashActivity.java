package com.strangedog.weylen.mthc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.google.gson.JsonObject;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.activity.login.LoginActivity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.prefs.NewVersionData;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-06.
 */
public class SplashActivity extends BaseActivity {

    private boolean isSettingNetwork = false;
    private static final int SLEEP_TIME = 2 * 1000; // 休眠时间2秒钟
    private long start;
    private long end;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    private void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前无网络连接")
                .setCancelable(false)
                .setPositiveButton("设置", (dialog, which) -> {
                    peekInSetting();
                })
                .setNegativeButton("退出", (dialog, which) ->{
                    finish();
                })
                .show();
    }

    private void peekInSetting(){
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
        isSettingNetwork = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        start = System.currentTimeMillis();
        // 判断是否有网络连接
        if (!LocaleUtil.isNetworkConnected(this)){
            isSettingNetwork = true;
            showAlertDialog();
        }else {
            peekInHome();
            checkNewVersion();
        }
    }

    private void peekInHome(){
        end = System.currentTimeMillis();
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, isSettingNetwork ? 300 : SLEEP_TIME - (end - start));
    }

    private void checkNewVersion() {
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .newVersion(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(jsonObject -> {
                    DebugUtil.d("SplashActivity 获取新版本:" + jsonObject);
                    if (ResponseMgr.getStatus(jsonObject) == 1) {
                        NewVersionData newVersionData = NewVersionData.INSTANCE;
                        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
                        String vNum = dataObject.get("v_n").getAsString();
                        try {
                            String name = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                            DebugUtil.d("SplashActivity vNum:" + vNum + ".name:" + name);
                            // 有新版本
                            if (name != null && !name.equalsIgnoreCase(vNum)) {
                                newVersionData.isNewVersion = true;
                                newVersionData.downloadUrl = Constants.BASE_URL + dataObject.get("path").getAsString();
                                newVersionData.desc = dataObject.get("context").getAsString();
                                newVersionData.isMust = dataObject.get("update").getAsInt() == 1;
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
