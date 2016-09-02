package com.strangedog.weylen.mthc.activity.stock;

import com.strangedog.weylen.mthc.entity.StockEntity;

import java.util.List;

/**
 * Created by weylen on 2016-08-29.
 */
public interface StockView {
    void onStartList();
    void onListFailure();
    void onListSuccess(List<StockEntity> data, boolean isComplete);
    void onLoadMoreFailure();
    void onLoadMoreSuccess(List<StockEntity> data, boolean isComplete);
}
