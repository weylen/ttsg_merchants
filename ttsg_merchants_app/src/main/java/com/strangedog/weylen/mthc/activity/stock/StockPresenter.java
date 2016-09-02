package com.strangedog.weylen.mthc.activity.stock;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.entity.StockEntity;
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
public class StockPresenter {

    private StockView stockView;
    public StockPresenter(StockView stockView){
        this.stockView = stockView;
    }

    public void start(String name, String limit){
        StockData.INSTANCE.limit = limit;
        StockData.INSTANCE.name = name;
        StockData.INSTANCE.pageNum = 1;
        stockView.onStartList();
        remote(name, limit, 1);
    }

    public void refresh(){
        remote(StockData.INSTANCE.name, StockData.INSTANCE.limit, 1);
    }

    public void loadMore(){
        remote(StockData.INSTANCE.name, StockData.INSTANCE.limit, StockData.INSTANCE.pageNum + 1);
    }

    private void remote(String name, String limit, int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .stockQuery(name, limit, pageNum)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("StockPresenter 查询库存失败：" + e.getMessage());
                        error(pageNum);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("StockPresenter 查询库存成功：" + jsonObject);
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
            stockView.onListFailure();
        }else {
            stockView.onLoadMoreFailure();
        }
    }

    private void map(JsonObject jsonObject){
        int pageNum = jsonObject.get("pageNum").getAsInt();
        int maxPage = jsonObject.get("maxPage").getAsInt();
        StockData.INSTANCE.isComplete = pageNum == maxPage;

        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        JsonObject imgObject = dataObject.get("img").getAsJsonObject();
        Gson gson = new Gson();
        List<StockEntity> data = gson.fromJson(dataObject.get("data").getAsJsonArray(), new TypeToken<List<StockEntity>>(){}.getType());
        for (StockEntity entity : data){
            String imgId = entity.getImg();
            entity.setImg(imgObject.get(imgId).getAsString());
        }

        if (pageNum == 1){
            stockView.onListSuccess(data, StockData.INSTANCE.isComplete);
        }else {
            stockView.onLoadMoreSuccess(data, StockData.INSTANCE.isComplete);
        }
    }
}
