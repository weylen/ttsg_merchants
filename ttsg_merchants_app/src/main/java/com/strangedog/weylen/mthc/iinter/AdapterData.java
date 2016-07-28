package com.strangedog.weylen.mthc.iinter;

import java.util.List;

/**
 * Created by Administrator on 2016-07-02.
 */
public interface AdapterData<T> {

    /**
     * 设置适配器数据
     * @param data
     */
    void setData(List<T> data);

    /**
     * 获取适配器数据
     * @return
     */
    List<T> getData();

    /**
     * 添加一组数据
     * @param newData
     */
    void addData(List<T> newData);

    /**
     * 添加一个数据
     * @param entity
     */
    void addData(T entity);

    /**
     * 更新数据
     * @param position
     * @param entity
     */
    void updateData(int position, T entity);

    /**
     * 移除数据
     * @param position
     */
    void removeData(int position);

    /**
     * 获取下标对应的数据
     * @param position
     * @return
     */
    T getItem(int position);

}
