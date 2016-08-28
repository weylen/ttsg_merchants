package com.strangedog.weylen.mthc.activity.addgoods;

import com.google.gson.JsonObject;

/**
 * Created by weylen on 2016-07-30.
 */
public enum AddGoodsData {

    INSTANCE;

    String keyword = "";
    String typeId = "";

    int pageNum = 1;
    boolean isComplete;

    void reset(){
        keyword = "";
        pageNum = 1;
        typeId = "";
        isComplete = false;
    }

    public JsonObject kindData = null;
}
