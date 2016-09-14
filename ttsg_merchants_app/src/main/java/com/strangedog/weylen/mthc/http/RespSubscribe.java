package com.strangedog.weylen.mthc.http;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.strangedog.weylen.mthc.util.DebugUtil;

import rx.Subscriber;

/**
 * Created by zhou on 2016/9/14.
 */
public class RespSubscribe extends Subscriber<JsonObject> {

    private static final int TOKEN_INVALID = -10;

    private Subscriber<JsonObject> realSubscriber;
    public RespSubscribe(Subscriber<JsonObject> subscriber){
        this.realSubscriber = Preconditions.checkNotNull(subscriber, "subscriber can not be null");
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        realSubscriber.onError(e);
    }

    @Override
    public void onNext(JsonObject jsonObject) {
        if (ResponseMgr.getStatus(jsonObject) == TOKEN_INVALID){
            DebugUtil.d("RespSubscribe jsonObject:" + jsonObject);
            ReloginDialog.showDialog();
            onError(new Exception("Token Invalid"));
        }else {
            realSubscriber.onNext(jsonObject);
        }
    }
}
