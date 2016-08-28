package com.strangedog.weylen.mthc.activity.orderdetails;

import com.strangedog.weylen.mthc.entity.OrderDetailsEntity;

import java.util.List;

/**
 * Created by weylen on 2016-08-14.
 */
public interface OrderDetailsView {
    void onStartRequestOrderDetails();
    void onRequestOrderDetailsSuccess(List<OrderDetailsEntity> data);
    void onRequestOrderDetailsFailure();

    void onStartAlertStatus();
    void onAlertStatusFailure(int status);
    void onAlertStatusSuccess(int status);
}
