package com.strangedog.weylen.mthc.activity.shelvesgoos;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.BasePresenter;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-03.
 */
public class InSellingPresenter implements BasePresenter{

    private GoodsView goodsView;
    public InSellingPresenter(GoodsView goodsView){
        this.goodsView = Preconditions.checkNotNull(goodsView);
    }

    @Override
    public void start() {

    }

    /**
     * 加载数据
     * @param keyword 商品搜索关键字 不传则获取默认的商品列表
     * @param status 上架为1 下架为2
     */
    public void load(String keyword, int status){
        InSellingData.INSTANCE.reset();
        InSellingData.INSTANCE.keyword = keyword;
        InSellingData.INSTANCE.status = status;
        goodsView.onStartLoading();
        getRemoteData(keyword, status, 1);
    }

    /**
     * 加载更多
     */
    public void loadMore(){
        goodsView.onStartLoadMore();
        getRemoteData(InSellingData.INSTANCE.keyword,  InSellingData.INSTANCE.status,
                ShelvesGoodsData.INSTANCE.pageNum + 1);
    }

    public void refresh(){
        goodsView.onStartRefresh();
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
            goodsView.onLoadMoreFailure();
        }else{
            goodsView.onLoadFailure();
        }
    }

    private void parse(JsonObject s){
        Gson gson = new Gson();
        List<ProductsEntity> data = gson.fromJson(s.get("data").getAsJsonArray(), new TypeToken<List<ProductsEntity>>(){}.getType());
        int maxPage = s.get("maxPage").getAsInt();
        int pageNum = s.get("pageNum").getAsInt();

        // 保存当前页面
        InSellingData.INSTANCE.pageNum = pageNum;
        InSellingData.INSTANCE.isComplete = maxPage == pageNum;
        if (pageNum > 1){
            goodsView.onLoadMoreSuccess(data, maxPage == pageNum);
        }else{
            goodsView.onLoadSuccess(data, maxPage == pageNum);
        }
    }

    /**
     * 下架商品
     * @param entity
     */
    public void downGoods(ProductsEntity entity){
        ArrayList<ProductsEntity> list = new ArrayList<>();
        list.add(entity);
        downGoods(list);
    }

    /**
     * 下架商品
     * @param upGoodsData
     */
    public void downGoods(List<ProductsEntity> upGoodsData){
        // 开始下架商品
        goodsView.onStartUpGoods();
        formatDownGoods(upGoodsData, 2);
    }

    /**
     * 转换需要下架的商品数据
     * @param upGoodsData
     */
    private void formatDownGoods(List<ProductsEntity> upGoodsData, int status){
        Observable.just(upGoodsData)
                .observeOn(Schedulers.io())
                .map(productsEntities -> LocaleUtil.formatUpDownStr(status, upGoodsData))
                .observeOn(Schedulers.io())
                .subscribe(s -> {
                    requestDownGoods(upGoodsData, s);
                });
    }

    /**
     * 下架商品
     * @param upGoodsData
     * @param param
     */
    private void requestDownGoods(List<ProductsEntity> upGoodsData, String param){
        DebugUtil.d("Presenter requestUpGoods param:" + param);
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .upDownGoods(param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        goodsView.onUpGoodsFailure();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("SellingGoodsPresenter getRemoteData onNext s:" + s);
                        if (ResponseMgr.getStatus(s) != 1){
                            goodsView.onUpGoodsFailure();
                        }else {
                            goodsView.onUpGoodsSuccess(upGoodsData);
                        }
                    }
                });
    }
}
