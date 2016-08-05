package com.strangedog.weylen.mthc;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.strangedog.weylen.mthc.view.ZProgressDialog;

/**
 * Created by Administrator on 2016-06-17.
 */
public class BaseActivity extends AppCompatActivity{

    private Toast mToast;

    protected void showToast(String message){
        if (mToast == null){
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        }
        mToast.setText(message);
        mToast.show();
    }

    private static ZProgressDialog progressDialog;
    protected synchronized void showProgressDialog(String message){
        dismissProgressDialog();
        progressDialog = ZProgressDialog.show(this, message);
    }

    protected synchronized void dismissProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    protected void showSnakeBar(View containerView, String message){
        Snackbar.make(containerView, message, Snackbar.LENGTH_SHORT).show();
    }
}
