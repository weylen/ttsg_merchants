package com.strangedog.weylen.mthc.activity.orderdetails;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.entity.OrderDetailsEntity;
import com.strangedog.weylen.mthc.entity.OrderDetailsProductsEntity;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.RespSubscribe;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderDetailsPresenter {

    private String orderId;
    private OrderDetailsView orderDetailsView;
    public OrderDetailsPresenter(OrderDetailsView orderDetailsView){
        this.orderDetailsView = orderDetailsView;
    }

    public void onRefresh(){
        remote(orderId);
    }

    public void onStartRequestOrderDetails(String orderId){
        orderDetailsView.onStartRequestOrderDetails();
        remote(orderId);
    }

    void remote(String orderId){
        this.orderId = orderId;
        DebugUtil.d("OrderDetailsPresenter 获取订单详情：" + orderId);
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getOrderDetails(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("OrderDetailsPresenter 获取订单详情失败：" + e.getMessage());
                        orderDetailsView.onRequestOrderDetailsFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("OrderDetailsPresenter 获取订单详情成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            map(jsonObject);
                        }else {
                            orderDetailsView.onRequestOrderDetailsFailure();
                        }
                    }
                }));
    }

    void map(JsonObject jsonObject){
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        Gson gson = new Gson();

        List<OrderDetailsEntity> listOrders = new ArrayList<>();
        JsonArray dataArray = dataObject.get("data").getAsJsonArray();
        int size = dataArray.size();
        for (int i = 0; i < size; i++){
            JsonObject item = dataArray.get(i).getAsJsonObject();
            String orderId = item.get("orderId").getAsString();
            String total = item.get("total").getAsString();
            String prepay_id = item.get("prepay_id").getAsString();
            ArrayList<OrderDetailsProductsEntity> products = gson.fromJson(item.get("products").getAsJsonArray(),
                    new TypeToken<ArrayList<OrderDetailsProductsEntity>>(){}.getType());

            OrderDetailsEntity orderEntity = new OrderDetailsEntity(orderId, total, prepay_id, products);
            listOrders.add(orderEntity);
        }

        // 图片JsonObject
        /**
         * 暂时应该不需要图片 先不用解析
         JsonObject imgObject = dataObject.get("img").getAsJsonObject();
         for (OrderEntity entity : listOrders){
         List<OrderEntity.ProductsEntity> productsEntities = entity.getProducts();
         for (OrderEntity.ProductsEntity productsEntity : productsEntities){
         productsEntity.setImg(imgObject.get(productsEntity.getImg()).getAsString());
         }
         }
         **/
        orderDetailsView.onRequestOrderDetailsSuccess(listOrders);
    }

    /**
     * 修改订单状态
     * @param orderId
     * @param status "1"："订单完成" "2"："订单未支付" "3"："订单已支付未发货" "4"："客户退货" "5"："客户取消订单" "6"："支付结果确认中" "7"："商家已结单" "6"："商家已送达"
     */
    void alertOrderStatus(String orderId, int status){
        orderDetailsView.onStartAlertStatus();
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .alertOrderStatus(orderId, String.valueOf(status))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        orderDetailsView.onAlertStatusFailure(status);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            orderDetailsView.onAlertStatusFailure(status);
                        }else{
                            orderDetailsView.onAlertStatusSuccess(status);
                        }
                    }
                }));
    }
}
