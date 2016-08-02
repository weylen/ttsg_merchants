package com.strangedog.weylen.mthc.activity.shelvesgoos;

import com.strangedog.weylen.mthc.entity.ProductsEntity;

import java.util.List;

/**
 * Created by weylen on 2016-08-03.
 */
public interface ShelvesGoodsView {

    void onStartLoading();
    void onLoadSuccess(List<ProductsEntity> listData, boolean isComplete);
    void onLoadFailure();
    void onStartLoadMore();
    void onLoadMoreFailure();
    void onLoadMoreSuccess(List<ProductsEntity> listData, boolean isComplete);
    void onStartRefresh();
    boolean isActive();
}
