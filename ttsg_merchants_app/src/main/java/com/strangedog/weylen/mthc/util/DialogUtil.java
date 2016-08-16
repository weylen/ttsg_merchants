package com.strangedog.weylen.mthc.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by weylen on 2016-08-15.
 */
public class DialogUtil {

    public static final void showAlertDialog(Context context, String message, DialogInterface.OnClickListener confirmListener){
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", confirmListener)
                .show();
    }
}
