package com.strangedog.weylen.mthc.activity.addgoods;

import com.google.gson.JsonObject;

/**
 * Created by weylen on 2016-07-30.
 */
public enum AddGoodsData {

    INSTANCE;

    String keyword = "";

    int pageNum = 1;

    void reset(){
        keyword = "";
        pageNum = 1;
    }

    JsonObject kindData = null;
}
