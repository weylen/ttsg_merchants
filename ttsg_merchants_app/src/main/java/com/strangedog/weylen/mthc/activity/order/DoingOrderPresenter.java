package com.strangedog.weylen.mthc.activity.order;

import com.google.common.base.Preconditions;

/**
 * Created by zhou on 2016/8/4.
 */
public class DoingOrderPresenter {

    private OrderView orderView;
    public DoingOrderPresenter(OrderView orderView){
        this.orderView = Preconditions.checkNotNull(orderView, "orderView cannot be null");
    }

    public void startLoad(){}

    public void refresh(){
        orderView.onLoadFailure();
    }

    public void loadMore(){}

    public void receiveOrder(){}
}
