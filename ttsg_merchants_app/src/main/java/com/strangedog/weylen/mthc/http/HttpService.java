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

    /**
     * 注销
     * @return
     */
    @POST("scca-logout")
    Observable<JsonObject> logout();

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
     * 修改商品信息
     * @return
     */
    @FormUrlEncoded
    @POST("cpma-update")
    Observable<JsonObject> alertGoodsInfo(
            @Field("cbList") String info
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

    /**
     * 获取订单列表
     * @param begin 开始时间
     * @param end 结束时间
     * @param pageNum 页码
     * @param type  传1表示完成订单，2表示进行中，不传表示全部
     * @return
     */
    @FormUrlEncoded
    @POST("oda-saleDetail")
    Observable<JsonObject> getOrders(
            @Field("begin") String begin,
            @Field("end") String end,
            @Field("pageNum") int pageNum,
            @Field("key") int type
    );

    /**
     * 获取销售详情
     * @param begin 开始时间
     * @param end 结束时间
     * @param pageNum 页码
     * @return
     */
    @FormUrlEncoded
    @POST("psda-saleDetail")
    Observable<JsonObject> getSaleDetails(
            @Field("begin") String begin,
            @Field("end") String end,
            @Field("pageNum") int pageNum
    );

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("oda-orderDetail")
    Observable<JsonObject> getOrderDetails(
            @Field("key") String orderId
    );

    /**
     * 修改订单状态
     * @param orderId 订单号
     * @param status "1"："订单完成" "2"："订单未支付" "3"："订单已支付未发货" "4"："客户退货" "5"："客户取消订单" "6"："支付结果确认中" "7"："商家已结单" "6"："商家已送达"
     * @return
     */
    @FormUrlEncoded
    @POST("oda-modify")
    Observable<JsonObject> alertOrderStatus(
            @Field("key") String orderId,
            @Field("end") String status
    );


}
