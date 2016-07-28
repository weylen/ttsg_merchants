package com.strangedog.weylen.mthc.iinter;

import android.view.View;

/**
 * Created by weylen on 2016-07-09.
 * item视图被点击的时候响应接口
 */
public interface ItemViewClickListener {
    /**
     * 视图被点击的时候响应
     * @param view 点击的视图对象
     * @param position 点击的item的下标
     */
    void onViewClick1(View view, int position);

    /**
     * 视图被点击的时候响应
     * @param view 点击的视图对象
     * @param position 点击的item的下标
     */
    void onViewClick2(View view, int position);

    /**
     * 视图被点击的时候响应
     * @param view 点击的视图对象
     * @param position 点击的item的下标
     */
    void onViewClick3(View view, int position);
}
