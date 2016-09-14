package com.strangedog.weylen.mthc.http;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.strangedog.weylen.mthc.BaseApplication;
import com.strangedog.weylen.mthc.activity.login.LoginActivity;
import com.strangedog.weylen.mthc.util.DebugUtil;

import rx.Subscriber;

/**
 * Created by zhou on 2016/9/14.
 */
public class RespSubscribe extends Subscriber<JsonObject> {

    private static final int TOKEN_INVALID = -10;
    private static final int SUCCESS = 1;

    private Subscriber<JsonObject> realSubscriber;
    public RespSubscribe(Subscriber<JsonObject> subscriber){
        this.realSubscriber = Preconditions.checkNotNull(subscriber, "subscriber can not be null");
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        realSubscriber.onError(e);
    }

    @Override
    public void onNext(JsonObject jsonObject) {
        if (ResponseMgr.getStatus(jsonObject) == TOKEN_INVALID){
            showDialog();
            onError(new Exception("Token Invalid"));
        }else {
            realSubscriber.onNext(jsonObject);
        }
    }

    private boolean isDialogShow;
    private void showDialog(){
        if (isDialogShow){
            DebugUtil.d("RespSubscribe Token Invalid Dialog is Showing");
            return;
        }
        if (BaseApplication.INSTANCE == null){
            DebugUtil.d("RespSubscribe Token Invalid but BaseApplication.INSTANCE == null");
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(BaseApplication.INSTANCE)
                .setTitle("提示")
                .setMessage("登录过期，请重新登录")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("登录", (dialog, which) -> {
                    dialog.dismiss();
                    enterInLogin(BaseApplication.INSTANCE);
                })
                .create();
        alertDialog.setOnDismissListener(dialog -> isDialogShow = false);
        alertDialog.setOnShowListener(dialog -> isDialogShow = true);
        alertDialog.show();
        isDialogShow = true;
    }

    private void enterInLogin(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
