package com.strangedog.weylen.mthc.activity.order;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.entity.OrderDetailsEntity;
import com.strangedog.weylen.mthc.entity.OrderDetailsProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
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
 * Created by zhou on 2016/8/4.
 */
public class DoingOrderPresenter {

    private OrderView orderView;
    public DoingOrderPresenter(OrderView orderView){
        this.orderView = Preconditions.checkNotNull(orderView, "orderView cannot be null");
    }

    public void startLoad(){
        orderView.onStartLoading();
        remote(Constants.EMPTY_STR, Constants.EMPTY_STR, 1);
    }

    public void refresh(boolean isShowProgress){
        if (isShowProgress){
            orderView.onStartRefresh();
        }
        remote(Constants.EMPTY_STR, Constants.EMPTY_STR, 1);
    }

    public void loadMore(){
        orderView.onStartLoadMore();
        remote(Constants.EMPTY_STR, Constants.EMPTY_STR, DoingOrderData.INSTANCE.pageNum + 1);
    }

    public void receiveOrder(){}

    void remote(String begin, String end, int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getOrders(begin, end, pageNum, 2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("DoingOrderPresenter 获取订单失败：" + e.getMessage());
                        error(pageNum);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("DoingOrderPresenter 获取订单成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            map(jsonObject);
                        }else {
                            error(pageNum);
                        }
                    }
                }));
    }



    private void error(int pageNum){
        if (pageNum > 1){
            orderView.onLoadMoreFailure();
        }else {
            orderView.onLoadFailure();
        }
    }

    private void map(JsonObject jsonObject){
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        int maxPage = jsonObject.get("maxPage").getAsInt();
        int currentPage = jsonObject.get("pageNum").getAsInt();
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
                    new TypeToken<ArrayList< OrderDetailsProductsEntity>>(){}.getType());

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

        boolean isFinish = maxPage == currentPage;
        // 记录当前的页码
        DoingOrderData.INSTANCE.pageNum = currentPage;
        DoingOrderData.INSTANCE.isComplete = isFinish;
        if (currentPage == 1){ // 刷新或者首次加载
            orderView.onLoadSuccess(listOrders, isFinish);
        }else{ // 加载更多
            orderView.onLoadMoreSuccess(listOrders, isFinish);
        }
    }

    /**
     * 修改订单状态
     * @param orderId
     * @param status "1"："订单完成" "2"："订单未支付" "3"："订单已支付未发货" "4"："客户退货" "5"："客户取消订单" "6"："支付结果确认中" "7"："商家已结单" "6"："商家已送达"
     */
    void alertOrderStatus(String orderId, int status, int position){
        orderView.onStartAlertStatus();
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
                        orderView.onAlertStatusFailure(position, status);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            orderView.onAlertStatusFailure(position, status);
                        }else{
                            orderView.onAlertStatusSuccess(position, status);
                        }
                    }
                }));
    }
}
