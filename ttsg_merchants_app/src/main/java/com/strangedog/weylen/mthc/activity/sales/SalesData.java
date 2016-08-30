package com.strangedog.weylen.mthc.activity.sales;

/**
 * Created by weylen on 2016-08-23.
 */
public enum SalesData {
    INSTANCE;

    int pageNum = 1;
    boolean isComplete;

    String startTime;
    String endTime;

    void reset(){
        pageNum = 1;
        isComplete = false;
    }
}
