package com.strangedog.weylen.mthc.activity.stock;

/**
 * Created by weylen on 2016-08-23.
 */
public enum StockData {
    INSTANCE;

    int pageNum = 1;
    boolean isComplete;

    void reset(){
        pageNum = 1;
        isComplete = false;
    }
}
