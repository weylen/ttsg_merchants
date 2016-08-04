package com.strangedog.weylen.mthc.activity.order;

import com.strangedog.weylen.mthc.entity.OrderEntity;

import java.util.List;

/**
 * Created by zhou on 2016/8/4.
 */
public interface OrderView {

    void onStartLoading();
    void onLoadFailure();
    void onLoadSuccess(List<OrderEntity> orderEntityList);
    void onStartLoadMore();
    void onLoadMoreFailure();
    void onLoadMoreSuccess(List<OrderEntity> orderEntityList);
    void onStartRefresh();
    void onReceiveOrder(int position);
}
