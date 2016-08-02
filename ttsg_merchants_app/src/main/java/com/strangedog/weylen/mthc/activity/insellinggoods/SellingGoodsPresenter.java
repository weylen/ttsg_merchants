package com.strangedog.weylen.mthc.activity.insellinggoods;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.BasePresenter;
import com.strangedog.weylen.mthc.activity.addgoods.AddGoodsData;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-28.
 */
public class SellingGoodsPresenter implements BasePresenter {

    private SellingGoodsView sellingGoodsView;
    public SellingGoodsPresenter(SellingGoodsView sellingGoodsView){
        this.sellingGoodsView = Preconditions.checkNotNull(sellingGoodsView);
    }

    @Override
    public void start() {

    }

    /**
     * 加载数据
     * @param keyword 商品搜索关键字 不传则获取默认的商品列表
     * @param status 上架为1 下架为2
     */
    public void onLoad(String keyword, int status){
        InSellingData.INSTANCE.keyword = keyword;
        InSellingData.INSTANCE.status = status;
        sellingGoodsView.onStartLoading();
        getRemoteData(keyword, status, 1);
    }

    /**
     * 加载更多
     */
    public void onLoadmore(){
        sellingGoodsView.onStartLoadmore();
        getRemoteData(InSellingData.INSTANCE.keyword,  InSellingData.INSTANCE.status,
                InSellingData.INSTANCE.pageNum + 1);
    }

    public void refresh(){
        getRemoteData(InSellingData.INSTANCE.keyword,  InSellingData.INSTANCE.status, 1);
    }

    private void getRemoteData(String keyword, int status, int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getShopGoods(keyword, status, pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("获取异常" + e.getMessage());
                        doError(pageNum);
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("SellingGoodsPresenter getRemoteData onNext s:" + s);
                        if (ResponseMgr.getStatus(s) != 1){
                            doError(pageNum);
                        }else {
                            parse(s);
                        }
                    }
                });
    }

    /**
     * 处理请求错误
     * @param pageNum 页面数
     */
    private void doError(int pageNum){
        if (pageNum > 1){
            sellingGoodsView.onLoadmoreFailuer();
        }else{
            sellingGoodsView.onLoadFailure();
        }
    }

    private void parse(JsonObject s){
        Gson gson = new Gson();
        List<ProductsEntity> data = gson.fromJson(s.get("data").getAsJsonArray(), new TypeToken<List<ProductsEntity>>(){}.getType());
        int maxPage = s.get("maxPage").getAsInt();
        int pageNum = s.get("pageNum").getAsInt();
        // 保存当前页面
        InSellingData.INSTANCE.pageNum = pageNum;
        if (pageNum > 1){
            sellingGoodsView.onLoadmoreSuccessful(data, maxPage == pageNum);
        }else{
            sellingGoodsView.onLoadSuccessful(data, maxPage == pageNum);
        }
    }
}
