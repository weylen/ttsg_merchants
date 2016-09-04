package com.strangedog.weylen.mthc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.activity.login.LoginActivity;
import com.strangedog.weylen.mthc.util.LocaleUtil;

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
}
