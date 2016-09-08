package com.strangedog.weylen.mthc.activity.withdraw_record;

/**
 * Created by weylen on 2016-08-23.
 */
public enum RecordData {
    INSTANCE;

    int pageNum = 1;
    boolean isComplete;

    String name;
    String limit;

    void reset(){
        pageNum = 1;
        isComplete = false;
    }
}
