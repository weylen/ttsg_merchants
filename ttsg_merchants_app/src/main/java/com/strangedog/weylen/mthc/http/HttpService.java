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

    /**
     * 登录
     * @param name
     * @param pwd
     * @param sort 设备标识符，1苹果，2是安卓，3是其他
     * @param deviceToken
     * @return
     */
    @FormUrlEncoded
    @POST("scca-slogin")
    Observable<JsonObject> login(
            @Field("uname") String name, // 用户名
            @Field("upass") String pwd, // 密码
            @Field("sort") String sort,
            @Field("areaId") String deviceToken
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

    @POST("psda-csletm")
    Observable<JsonObject> getBalance();

    /**
     * 销售统计 开始时间和结束时间不传获取的是全部数据
     * @param proId 商品id
     * @param startTime 开始时间：格式2016-10-10 00:00:00
     * @param endTime 结束时间：格式2016-10-10 23:59:59
     * @param pageNum
     * @return
     */
    @FormUrlEncoded
    @POST("psda-salesStatistics")
    Observable<JsonObject> salesStatistical(
            @Field("proId") String proId,
            @Field("begin") String startTime,
            @Field("end") String endTime,
            @Field("pageNum") int pageNum
    );

    @FormUrlEncoded
    @POST("psda-lowStock")
    Observable<JsonObject> stockQuery(
            @Field("name") String name,
            @Field("limit") String limit,
            @Field("pageNum") int pageNum
    );

    /**
     * 提现申请
     * @param quota 申请金额
     * @return
     */
    @FormUrlEncoded
    @POST("aeca-apply")
    Observable<JsonObject> withdraw(
            @Field("quota") String quota
    );


    /**
     * 提现记录
     * @param pageNum
     * @return
     */
    @FormUrlEncoded
    @POST("aeca-")
    Observable<JsonObject> withdrawRecord(
            @Field("pageNum") int pageNum
    );

    /**
     * 获取促销商品
     * @param status 上架是1，下架是2
     * @param name 商品名称
     * @param id 小类类别id
     * @param pageNum 页码 默认是1
     * @return
     */
    @FormUrlEncoded
    @POST("cpma-salePromote")
    Observable<JsonObject> getPromotionProducts(
            @Field("cbList") String status,
            @Field("key") String name,
            @Field("kind") String id,
            @Field("pageNum") int pageNum
     );

    /**
     * 检查新版本
     * @param type 1是安卓商家版，2是安卓用户版
     * @return
     */
    @FormUrlEncoded
    @POST("scca-getLatestVersion")
    Observable<JsonObject> newVersion(
            @Field("sort") int type
    );


    /**
     * 获取商家的状态
     * @param areaId 商家id
     * @return
     */
    @FormUrlEncoded
    @POST("scca-getTradeState")
    Observable<JsonObject> getTradeState(
            @Field("areaId") String areaId
    );

    /**
     * 设置商家营业时间
     * @param time 营业时间，如9:00~23:00
     * @return
     */
    @FormUrlEncoded
    @POST("cia-shopTradeState")
    Observable<JsonObject> setTradeTimeState(
            @Field("text") String time
    );

    /**
     * 设置商家状态
     * @param status 1是正常营业，2是停业，3是休业
     * @return
     */
    @FormUrlEncoded
    @POST("cia-shopTradeState")
    Observable<JsonObject> setTradeState(
            @Field("key") int status
    );

    /**
     * 配送费设置
     * @param delivery 配送费
     * @param text 产生费用的下限金额
     * @return
     */
    @FormUrlEncoded
    @POST("cia-shopFareAmount")
    Observable<JsonObject> deliverySetting(
            @Field("key") String delivery,
            @Field("text") String text
    );

    /**
     * 置顶和取消置顶
     * @param key  商品id为1，如果置顶是1，取消是2则参数传递方式为：1-1或2-1
     * @return
     */
    @FormUrlEncoded
    @POST("cpma-proTop")
    Observable<JsonObject> alertTopStatus(
            @Field("Key") String key
    );
}
