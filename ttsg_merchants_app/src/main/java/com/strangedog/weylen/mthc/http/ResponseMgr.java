package com.strangedog.weylen.mthc.http;

import com.google.gson.JsonObject;

import org.json.JSONException;

/**
 * Created by weylen on 2016-07-21.
 */
public class ResponseMgr {

    /**
     * 获取数据状态
     * @return 1表示成功 其他看文档
     */
    public static int getStatus(JsonObject source) {
         return source.get("stauts").getAsInt();
    }


    /**
     * 获取data字段数据
     * @param source
     * @return
     * @throws JSONException
     */
    public static JsonObject getData(JsonObject source) {
        return source.get("data").getAsJsonObject();
    }
}
