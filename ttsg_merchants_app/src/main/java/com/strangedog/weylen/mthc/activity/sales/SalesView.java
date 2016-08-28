package com.strangedog.weylen.mthc.activity.sales;

import com.strangedog.weylen.mthc.entity.SalesEntity;

import java.util.List;

/**
 * 销售信息接口
 */
public interface SalesView {
    void onStartRequest();
    void onRequestFailure();
    void onRequestSuccess(List<SalesEntity> data, boolean isComplete);
    void onStartRefresh();
    void onStartLoadMore();
    void onLoadMoreFailure();
    void onLoadMoreSuccess(List<SalesEntity> data, boolean isComplete);
}
