package com.strangedog.weylen.mthc.activity.order;

import com.strangedog.weylen.mthc.entity.OrderDetailsEntity;

import java.util.List;

/**
 * Created by zhou on 2016/8/4.
 */
public interface OrderView {

    void onStartLoading();
    void onLoadFailure();
    void onLoadSuccess(List<OrderDetailsEntity> orderEntityList, boolean isComplete);
    void onStartLoadMore();
    void onLoadMoreFailure();
    void onLoadMoreSuccess(List<OrderDetailsEntity> orderEntityList, boolean isComplete);
    void onStartRefresh();
    void onReceiveOrder(int position);
    boolean isActive();
    void onStartAlertStatus();
    void onAlertStatusFailure(int position, int status);
    void onAlertStatusSuccess(int position, int status);
}
