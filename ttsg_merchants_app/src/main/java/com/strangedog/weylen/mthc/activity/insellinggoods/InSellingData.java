package com.strangedog.weylen.mthc.activity.insellinggoods;

import com.google.gson.JsonObject;

/**
 * Created by weylen on 2016-07-28.
 */
public enum InSellingData {
    INSTANCE;

    JsonObject s; // 保存的数据
    String keyword; // 搜索的关键字
}
