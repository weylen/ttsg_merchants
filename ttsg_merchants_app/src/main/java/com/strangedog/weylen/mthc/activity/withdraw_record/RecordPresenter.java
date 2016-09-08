package com.strangedog.weylen.mthc.activity.withdraw_record;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.activity.stock.StockData;
import com.strangedog.weylen.mthc.activity.stock.StockView;
import com.strangedog.weylen.mthc.entity.StockEntity;
import com.strangedog.weylen.mthc.entity.WithdrawRecordEntity;
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
public class RecordPresenter {

    private RecordView recordView;
    public RecordPresenter(RecordView recordView){
        this.recordView = recordView;
    }

    public void start(){
        RecordData.INSTANCE.pageNum = 1;
        recordView.onStartList();
        remote(1);
    }

    public void refresh(){
        remote(1);
    }

    public void loadMore(){
        remote(RecordData.INSTANCE.pageNum + 1);
    }

    private void remote(int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .withdrawRecord(pageNum)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("RecordPresenter 转出记录失败：" + e.getMessage());
                        error(pageNum);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("RecordPresenter 转出记录：" + jsonObject);
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
            recordView.onListFailure();
        }else {
            recordView.onLoadMoreFailure();
        }
    }

    private void map(JsonObject jsonObject){
        int pageNum = jsonObject.get("pageNum").getAsInt();
        int maxPage = jsonObject.get("maxPage").getAsInt();
        RecordData.INSTANCE.isComplete = pageNum == maxPage;
        RecordData.INSTANCE.pageNum = pageNum;

        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        Gson gson = new Gson();
        List<WithdrawRecordEntity> data = gson.fromJson(dataObject.get("data").getAsJsonArray(),
                new TypeToken<List<WithdrawRecordEntity>>(){}.getType());

        if (pageNum == 1){
            recordView.onListSuccess(data, RecordData.INSTANCE.isComplete);
        }else {
            recordView.onLoadMoreSuccess(data, RecordData.INSTANCE.isComplete);
        }
    }
}
