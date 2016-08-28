package com.strangedog.weylen.mthc.activity.sales;

import com.google.gson.JsonObject;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;

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
    void start(){
        DebugUtil.d("SalesPresenter start--------------");
        SalesData.INSTANCE.reset();
        salesView.onStartRequest();
        remote(Constants.EMPTY_STR, Constants.EMPTY_STR, 1);
    }

    void refresh(){
        DebugUtil.d("SalesPresenter refresh--------------");
        SalesData.INSTANCE.reset();
        salesView.onStartRefresh();
        remote(Constants.EMPTY_STR, Constants.EMPTY_STR, 1);
    }

    void loadMore(){
        DebugUtil.d("SalesPresenter loadMore--------------");
        salesView.onStartLoadMore();
        remote(Constants.EMPTY_STR, Constants.EMPTY_STR, SalesData.INSTANCE.pageNum + 1);
    }

    void remote(String begin, String end, int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getSaleDetails(begin, end, pageNum)
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
                            salesView.onRequestSuccess(null, false);
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
}
