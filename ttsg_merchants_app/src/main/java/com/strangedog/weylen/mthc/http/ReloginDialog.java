package com.strangedog.weylen.mthc.http;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

import com.strangedog.weylen.mthc.BaseApplication;
import com.strangedog.weylen.mthc.activity.login.LoginActivity;
import com.strangedog.weylen.mthc.activity.login.LoginData;
import com.strangedog.weylen.mthc.util.DebugUtil;

/**
 * Created by weylen on 2016-09-14.
 */
public class ReloginDialog {

    private static AlertDialog alertDialog;
    public static void showDialog(){
        if (alertDialog != null && alertDialog.isShowing()){
            DebugUtil.d("RespSubscribe Token Invalid Dialog is Showing");
            return;
        }
        final Context context = BaseApplication.getLast();
        if (context == null){
            DebugUtil.d("RespSubscribe Token Invalid but context == null");
            return;
        }
        alertDialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("登录过期，请重新登录")
                .setPositiveButton("登录", (dialog, which) -> {
                    dialog.dismiss();
                    enterInLogin(BaseApplication.INSTANCE);
                })
                .create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private static void enterInLogin(Context context){
        LoginData.INSTANCE.logout(context);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
