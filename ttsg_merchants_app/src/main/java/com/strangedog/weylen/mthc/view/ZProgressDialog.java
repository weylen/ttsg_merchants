package com.strangedog.weylen.mthc.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.rey.material.app.Dialog;
import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.util.DimensUtil;

/**
 * Created by weylen on 2016-07-08.
 */
public class ZProgressDialog extends Dialog {

    private String message;

    public ZProgressDialog(Context context) {
        super(context, R.style.Dialog_NoTitle);
    }

    public static ZProgressDialog show(Context context, String message){
        ZProgressDialog dialog = new ZProgressDialog(context);
        dialog.setMessage(message);
        dialog.show();
        return dialog;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_dialog);

        LinearLayout layout = (LinearLayout) findViewById(R.id.body);
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(metrics.widthPixels - DimensUtil.dp2px(getContext(), 80),
                -2);
        layout.setLayoutParams(params);

        TextView messageView = (TextView) findViewById(R.id.message);
        messageView.setText(TextUtils.isEmpty(message) ? "加载中..." : message);
    }
}
