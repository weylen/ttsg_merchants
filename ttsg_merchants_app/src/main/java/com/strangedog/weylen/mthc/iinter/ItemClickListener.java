package com.strangedog.weylen.mthc.iinter;

/**
 * Created by Administrator on 2016-07-03.
 */
public interface ItemClickListener<T> {

    /**
     * 列表item点击事件
     * @param t item内容数据
     * @param position 当前item下标
     */
    void onItemClicked(T t, int position);
}
