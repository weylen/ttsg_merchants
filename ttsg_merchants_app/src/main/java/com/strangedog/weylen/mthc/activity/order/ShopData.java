package com.strangedog.weylen.mthc.activity.order;

import com.strangedog.weylen.mthc.http.Constants;

import java.util.HashMap;

/**
 * Created by weylen on 2016-09-11.
 */
public enum ShopData {
    INSTANCE;

    String startTime = Constants.EMPTY_STR;
    String endTime = Constants.EMPTY_STR;
    String nightStart = Constants.EMPTY_STR;
    String nightEnd = Constants.EMPTY_STR;

    int status = 3;
    String fare = "0";
    String fareLimit = "0";

    private HashMap<Integer, String> statusStrs;

    ShopData(){
        statusStrs = new HashMap<>();
        statusStrs.put(1, "正在营业");
        statusStrs.put(2, "停业中");
        statusStrs.put(3, "休息中");
    }

    public String getStatus(){
        return statusStrs.get(status);
    }
}
