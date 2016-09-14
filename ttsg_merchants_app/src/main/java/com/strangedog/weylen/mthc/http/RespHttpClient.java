package com.strangedog.weylen.mthc.http;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zhou on 2016/9/14.
 */
public class RespHttpClient extends OkHttpClient{
    @Override
    public Call newCall(Request request) {
        Call call = super.newCall(request);
        return super.newCall(request);
    }


}
