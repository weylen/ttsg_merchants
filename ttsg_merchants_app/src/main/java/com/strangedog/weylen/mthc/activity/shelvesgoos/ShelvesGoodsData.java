package com.strangedog.weylen.mthc.activity.shelvesgoos;

import com.google.gson.JsonObject;

/**
 * Created by weylen on 2016-08-03.
 */
public enum ShelvesGoodsData {
    INSTANCE;

    JsonObject DATA; // 保存的数据
    String keyword; // 搜索的关键字
    String kindId = "";
    int status = 2; //  状态值
    int pageNum = 1;
    boolean isComplete;

    void reset(){
        DATA = null;
        keyword = null;
        status = 2;
        pageNum = 1;
        isComplete = false;
        kindId = "";
    }

}
