package com.strangedog.weylen.mthc.view;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;

/**
 * Created by weylen on 2016-07-08.
 */
public class ZEmptyViewHelper{

    private View emptyView;
    private View contentView;
    private ViewGroup containerView;
    private TextView mTextView;
    private OnEmptyViewClickListener onEmptyViewClickListener;

    /**
     * 界面显示的视图对象 默认显示自定义的空视图
     * @param contentView
     * @param inflater 布局转换器
     */
    public ZEmptyViewHelper(LayoutInflater inflater, @NonNull View contentView, @NonNull ViewGroup containerView){
        this(inflater.inflate(R.layout.layout_empty, null, false), contentView, containerView);
    }

    /**
     *
     * @param emptyView  空视图对象
     * @param contentView 内容对象
     */
    public ZEmptyViewHelper(@NonNull View emptyView, @NonNull View contentView, @NonNull ViewGroup containerView){
        this.emptyView = emptyView;
        this.contentView = contentView;
        this.containerView = containerView;
        init();
    }

    /**
     * 初始化内容
     */
    private void init(){
        mTextView = (TextView) emptyView.findViewById(R.id.emptyView);
        emptyView.setOnClickListener(emptyClick);
        // 添加空视图
        emptyView.setVisibility(View.GONE);// 默认隐藏空视图
        containerView.addView(emptyView);
    }

    /**
     * 设置空视图文本的内容
     * @param emptyText
     */
    public void setEmptyText(String emptyText){
        if (mTextView != null){
            mTextView.setText(emptyText);
        }
    }

    private View.OnClickListener emptyClick = v -> {
        if (onEmptyViewClickListener != null){
            onEmptyViewClickListener.onEmptyViewClick();
        }
    };

    public void setOnEmptyViewClickListener(OnEmptyViewClickListener onEmptyViewClickListener) {
        this.onEmptyViewClickListener = onEmptyViewClickListener;
    }

    /**
     * 空视图点击事件监听
     */
    public interface OnEmptyViewClickListener{
        /**
         * 当空视图被点击的时候响应
         */
        void onEmptyViewClick();
    }
}
