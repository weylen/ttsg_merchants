package com.strangedog.weylen.mthc.activity.promotion_goods;

import com.strangedog.weylen.mthc.entity.ProductsEntity;

import java.util.List;

/**
 * Created by weylen on 2016-08-29.
 */
public interface PromotionView {
    void onStartList();
    void onListFailure();
    void onListSuccess(List<ProductsEntity> data, boolean isComplete);
    void onLoadMoreFailure();
    void onLoadMoreSuccess(List<ProductsEntity> data, boolean isComplete);
}
