package com.strangedog.weylen.mthc.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by weylen on 2016-12-02.
 */
public class AlertDialogUtil {

    public static void showAlertDialog(Context context, String title, String message, String leftMsg, DialogInterface.OnClickListener
                                       leftClickListener, String rightMsg, DialogInterface.OnClickListener rightClickListener){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(leftMsg, leftClickListener)
                .setPositiveButton(rightMsg, rightClickListener)
                .show();
    }
}
