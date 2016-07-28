package com.strangedog.weylen.mthc.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rey.material.widget.ProgressView;
import com.strangedog.weylen.mtch.R;

/**
 * Created by Administrator on 2016/3/21.
 */
public class DefaultHeader extends BaseHeader {
    private int arrowSrc;
    private final int ROTATE_ANIM_DURATION = 180;
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;

//    private TextView headerTitle;
    private ImageView headerArrow;
    private ProgressView headerProgressbar;

    public DefaultHeader(){
        this(R.mipmap.abc_refresh_arrow);
    }

    public DefaultHeader(int arrowSrc){
        this.arrowSrc = arrowSrc;

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    @Override
    public View getView(LayoutInflater inflater,ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.default_header, viewGroup, true);
//        headerTitle = (TextView) view.findViewById(R.id.default_header_title);
        headerArrow = (ImageView) view.findViewById(R.id.default_header_arrow);
        headerProgressbar = (ProgressView) view.findViewById(R.id.default_header_progressbar);
        headerArrow.setImageResource(arrowSrc);
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        if (!upORdown){
//            headerTitle.setText("松开立即刷新");
            if (headerArrow.getVisibility()==View.VISIBLE)
                headerArrow.startAnimation(mRotateUpAnim);
        }
        else {
//            headerTitle.setText("下拉刷新");
            if (headerArrow.getVisibility()==View.VISIBLE)
                headerArrow.startAnimation(mRotateDownAnim);
        }
    }

    @Override
    public void onStartAnim() {
//        headerTitle.setText("正在刷新");
//        headerTitle.setVisibility(View.GONE);
        headerArrow.setVisibility(View.INVISIBLE);
        headerArrow.clearAnimation();
        headerProgressbar.setVisibility(View.VISIBLE);
        headerProgressbar.setProgress(0f);
        headerProgressbar.start();
    }

    @Override
    public void onFinishAnim() {
        headerArrow.setVisibility(View.VISIBLE);
        headerProgressbar.setVisibility(View.INVISIBLE);
        headerProgressbar.stop();
//        headerTitle.setVisibility(View.VISIBLE);
    }
}