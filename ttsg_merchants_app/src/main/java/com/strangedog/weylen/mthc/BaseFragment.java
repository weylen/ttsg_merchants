package com.strangedog.weylen.mthc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.widget.ProgressView;
import com.strangedog.weylen.mthc.view.ZProgressDialog;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-06-17.
 */
public abstract class BaseFragment extends Fragment{

    private Toast mToast;

    protected void showToast(String message){
        if (mToast == null){
            mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        }
        mToast.setText(message);
        mToast.show();
    }

    private ZProgressDialog progressDialog;
    protected void showProgressDialog(String message){

        progressDialog = ZProgressDialog.show(getActivity(), message);
    }

    protected void dismissProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    protected void showSnakeView(View containerView, String message){
        Snackbar.make(containerView, message, Snackbar.LENGTH_SHORT).show();
    }

    protected LayoutInflater getLayoutInflater(){
        return LayoutInflater.from(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutId(), container, false);
    }

    /**
     * 返回此fragment加载的布局的id
     * @return
     */
    public abstract int layoutId();


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
