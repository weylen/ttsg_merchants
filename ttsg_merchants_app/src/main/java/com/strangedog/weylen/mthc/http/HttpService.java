package com.strangedog.weylen.mthc.http;

import com.google.gson.JsonObject;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by weylen on 2016-07-20.
 */
public interface HttpService {

    @FormUrlEncoded
    @POST("scca-slogin")
    Observable<JsonObject> login(
            @Field("uname") String name, // 用户名
            @Field("upass") String pwd // 密码
    );

    @POST("scca-getKind")
    Observable<JsonObject> getKind();

    /**
     * 获取在售商品
     * @return
     */
    @FormUrlEncoded
    @POST("cpma-")
    Observable<JsonObject> getInSellingGoods(
            @Field("key") String key,
            @Field("pageNum") int pageNum
    );

    @FormUrlEncoded
    @POST("cpma-add")
    Observable<JsonObject> addProducts(
            @Field("cbList") String cb
    );


}
