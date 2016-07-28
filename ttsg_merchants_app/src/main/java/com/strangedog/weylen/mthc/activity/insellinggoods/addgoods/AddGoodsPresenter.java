package com.strangedog.weylen.mthc.activity.insellinggoods.addgoods;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.strangedog.weylen.mthc.BasePresenter;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.SessionUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-28.
 */
public class AddGoodsPresenter implements BasePresenter {

    private AddGoodsView addGoodsView;
    public AddGoodsPresenter(AddGoodsView addGoodsView){
        this.addGoodsView = Preconditions.checkNotNull(addGoodsView);
    }

    @Override
    public void start() {

    }

    /**
     * 加载数据
     * @param keyword 商品搜索关键字 不传则获取默认的商品列表
     */
    public void onLoad(String keyword){
        addGoodsView.onStartLoading();
        getRemoteData(keyword == null ? "" : keyword, 1);
    }

    /**
     * 加载更多
     */
    public void onLoadmore(){

    }

    /**
     * 处理请求错误
     * @param pageNum 页面数
     */
    private void doError(int pageNum){
        if (pageNum > 1){
            addGoodsView.onLoadmoreFailuer();
        }else{
            addGoodsView.onLoadFailure();
        }
    }

    private void getRemoteData(String keyword, int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getInSellingGoods(keyword, pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        DebugUtil.d("获取异常" + e.getMessage());
                        doError(pageNum);
                    }

                    @Override
                    public void onNext(Object s) {
                        DebugUtil.d("SellingGoodsPresenter getRemoteData onNext s:" + s);
//                        if (ResponseMgr.getStatus(s) != 1){
//                            doError(pageNum);
//                        }else {
//                            parse(s);
//                        }
                    }
                });
    }

    private void parse(JsonObject s){
        List<ProductsEntity> data = new ArrayList<>();
//        s.get("data");

    }
}
