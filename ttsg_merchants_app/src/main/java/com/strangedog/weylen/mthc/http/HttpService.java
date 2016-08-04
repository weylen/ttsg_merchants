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
     * 获取可添加的商品
     * @return
     */
    @FormUrlEncoded
    @POST("cpma-")
    Observable<JsonObject> addGoodsList(
            @Field("key") String key,
            @Field("pageNum") int pageNum,
            @Field("cbList") String typeId
    );

    @FormUrlEncoded
    @POST("cpma-add")
    Observable<JsonObject> addProducts(
            @Field("cbList") String cb
    );

    /**
     *
     * @param keyword 查询关键字
     * @param status 上架是1 下架是2
     * @param pageNum 页码
     * @return
     */
    @FormUrlEncoded
    @POST("cpma-salableCommodity")
    Observable<JsonObject> getShopGoods(
            @Field("key") String keyword,
            @Field("cbList") int status,
            @Field("pageNum") int pageNum,
            @Field("kind") String kindId
    );

    /**
     * 商品上下架
     * @param str
     * @return
     */
    @FormUrlEncoded
    @POST("cpma-updown")
    Observable<JsonObject> upDownGoods(
            @Field("Key") String str
    );
}
