package com.strangedog.weylen.mthc.activity.sales;

import com.strangedog.weylen.mthc.entity.SalesEntity;

import java.util.List;

/**
 * 销售信息接口
 */
public interface SalesView {
    void onStartRequest();
    void onRequestFailure();

    /**
     * 获取销售数据成功
     * @param data 数据
     * @param total 总额
     * @param isComplete 是否加载完所以的数据
     */
    void onRequestSuccess(List<SalesEntity> data, String total, boolean isComplete);
    void onStartRefresh();
    void onStartLoadMore();
    void onLoadMoreFailure();
    /**
     * 获取更多销售数据成功
     * @param data 数据
     * @param total 总额
     * @param isComplete 是否加载完所以的数据
     */
    void onLoadMoreSuccess(List<SalesEntity> data, String total, boolean isComplete);
}
