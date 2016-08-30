package com.strangedog.weylen.mthc.activity.sales;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.entity.SalesEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-23.
 */
public class SalesPresenter {

    private SalesView salesView;
    public SalesPresenter(SalesView salesView){
        this.salesView = salesView;
    }

    /**
     * 开始加载销售列表数据
     */
    void start(String startTime, String endTime){
        SalesData.INSTANCE.reset();
        salesView.onStartRequest();
        SalesData.INSTANCE.startTime = startTime;
        SalesData.INSTANCE.endTime = endTime;
        remote(Constants.EMPTY_STR, startTime, endTime, 1);
    }

    void refresh(){
        SalesData.INSTANCE.reset();
        salesView.onStartRefresh();
        remote(Constants.EMPTY_STR, SalesData.INSTANCE.startTime, SalesData.INSTANCE.endTime, 1);
    }

    void loadMore(){
        salesView.onStartLoadMore();
        remote(Constants.EMPTY_STR, SalesData.INSTANCE.startTime, SalesData.INSTANCE.endTime, SalesData.INSTANCE.pageNum + 1);
    }

    void remote(String proId,String begin, String end, int pageNum){
        DebugUtil.d("SalesPresenter 开始时间：" + begin + ", 结束时间：" + end);

        RetrofitFactory.getRetrofit().create(HttpService.class)
                .salesStatistical(proId, begin, end, pageNum)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("SalesPresenter 获取销售信息失败："+e.getMessage());
                        error(pageNum);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("SalesPresenter 获取销售信息成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            parse(jsonObject);
                        }else {
                            error(pageNum);
                        }
                    }
                });
    }

    private void error(int pageNum){
        if (pageNum > 1){
            salesView.onLoadMoreFailure();
        }else {
            salesView.onRequestFailure();
        }
    }

    private void parse(JsonObject jsonObject){
        int maxPage = jsonObject.get("maxPage").getAsInt();
        int pageNum = jsonObject.get("pageNum").getAsInt();
        SalesData.INSTANCE.pageNum = pageNum;
        SalesData.INSTANCE.isComplete = maxPage == pageNum;

        // 解析
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        JsonObject imgObject = dataObject.get("img").getAsJsonObject();
        Gson gson = new Gson();
        List<SalesEntity> dataList = gson.fromJson(dataObject.get("data").getAsJsonArray(), new TypeToken<List<SalesEntity>>(){}.getType());
        // 图片
        if (!LocaleUtil.isListEmpty(dataList)){
            for (SalesEntity entity : dataList){
                String imgId = entity.getImg();
                entity.setImg(imgObject.get(imgId).getAsString().split(",")[0]);
            }
        }
        // 获取销售总额
        String total = dataObject.get("total").getAsString();
        // 处理结果
        if (pageNum > 1){
            salesView.onLoadMoreSuccess(dataList, total, SalesData.INSTANCE.isComplete);
        }else {
            salesView.onRequestSuccess(dataList, total, SalesData.INSTANCE.isComplete);
        }
    }
}
