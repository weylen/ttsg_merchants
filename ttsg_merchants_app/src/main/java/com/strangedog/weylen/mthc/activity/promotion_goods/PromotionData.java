package com.strangedog.weylen.mthc.activity.promotion_goods;

/**
 * Created by weylen on 2016-08-23.
 */
public enum PromotionData {
    INSTANCE;

    int pageNum = 1;
    boolean isComplete;

    String id;
    String status;

    void reset(){
        pageNum = 1;
        isComplete = false;
    }
}
