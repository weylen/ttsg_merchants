package com.strangedog.weylen.mthc.activity.promotion_goods;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.activity.withdraw_record.RecordData;
import com.strangedog.weylen.mthc.activity.withdraw_record.RecordView;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.entity.PromotionEntity;
import com.strangedog.weylen.mthc.entity.WithdrawRecordEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-29.
 */
public class PromotionPresenter {

    private PromotionView promotionView;
    public PromotionPresenter(PromotionView promotionView){
        this.promotionView = promotionView;
    }

    public void start(String status, String id){
        PromotionData.INSTANCE.pageNum = 1;
        PromotionData.INSTANCE.status = status;
        PromotionData.INSTANCE.id = id;
        promotionView.onStartList();
        remote(status, id, 1);
    }

    public void refresh(){
        remote(PromotionData.INSTANCE.status, PromotionData.INSTANCE.id, 1);
    }

    public void loadMore(){
        remote(PromotionData.INSTANCE.status, PromotionData.INSTANCE.id, PromotionData.INSTANCE.pageNum + 1);
    }

    private void remote(String status, String id, int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getPromotionProducts(status, Constants.EMPTY_STR, id, pageNum)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("PromotionPresenter 获取促销商品：" + e.getMessage());
                        error(pageNum);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("PromotionPresenter 获取促销商品：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            map(jsonObject);
                        }else {
                            error(pageNum);
                        }
                    }
                });
    }

    private void error(int pageNum){
        if (pageNum == 1){
            promotionView.onListFailure();
        }else {
            promotionView.onLoadMoreFailure();
        }
    }

    private void map(JsonObject jsonObject){
        int pageNum = jsonObject.get("pageNum").getAsInt();
        int maxPage = jsonObject.get("maxPage").getAsInt();
        PromotionData.INSTANCE.isComplete = pageNum == maxPage;
        PromotionData.INSTANCE.pageNum = pageNum;

        Gson gson = new Gson();
        List<ProductsEntity> data = gson.fromJson(jsonObject.get("data").getAsJsonArray(),
                new TypeToken<List<ProductsEntity>>(){}.getType());

        if (pageNum == 1){
            promotionView.onListSuccess(data, PromotionData.INSTANCE.isComplete);
        }else {
            promotionView.onLoadMoreSuccess(data, PromotionData.INSTANCE.isComplete);
        }
    }
}
