package com.strangedog.weylen.mthc.activity.shelvesgoos;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.BasePresenter;
import com.strangedog.weylen.mthc.activity.addgoods.AddGoodsData;
import com.strangedog.weylen.mthc.entity.KindDataEntity;
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
public class ShelvesPresenter implements BasePresenter{

    private GoodsView goodsView;
    public ShelvesPresenter(GoodsView goodsView){
        this.goodsView = Preconditions.checkNotNull(goodsView);
    }

    @Override
    public void start() {

    }

    /**
     * 加载数据
     * @param keyword 商品搜索关键字 不传则获取默认的商品列表
     * @param status 上架为1 下架为2
     * @param kindId 小类类型id
     */
    public void load(String keyword, int status, String kindId){
        ShelvesGoodsData.INSTANCE.reset();
        ShelvesGoodsData.INSTANCE.keyword = keyword;
        ShelvesGoodsData.INSTANCE.status = status;
        ShelvesGoodsData.INSTANCE.kindId = kindId;
        goodsView.onStartLoading();
        getRemoteData(keyword, status, 1, kindId);
    }

    /**
     * 加载更多
     */
    public void loadMore(){
        goodsView.onStartLoadMore();
        getRemoteData(ShelvesGoodsData.INSTANCE.keyword,  ShelvesGoodsData.INSTANCE.status,
                ShelvesGoodsData.INSTANCE.pageNum + 1,
                ShelvesGoodsData.INSTANCE.kindId);
    }

    public void refresh(boolean isShowProgress){
        if (isShowProgress){
            goodsView.onStartRefresh();
        }
        getRemoteData(ShelvesGoodsData.INSTANCE.keyword,  ShelvesGoodsData.INSTANCE.status, 1,
                ShelvesGoodsData.INSTANCE.kindId);
    }

    private void getRemoteData(String keyword, int status, int pageNum, String kindId){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getShopGoods(keyword, status, pageNum, kindId)
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
        ShelvesGoodsData.INSTANCE.pageNum = pageNum;
        ShelvesGoodsData.INSTANCE.isComplete = maxPage == pageNum;
        if (pageNum > 1){
            goodsView.onLoadMoreSuccess(data, maxPage == pageNum);
        }else{
            goodsView.onLoadSuccess(data, maxPage == pageNum);
        }
    }

    /**
     * 上架商品
     */
    public void upGoods(List<ProductsEntity> upGoodsData){
        // 开始上架商品
        goodsView.onStartUpGoods();
        formatUpGoods(upGoodsData, 1);
    }

    /**
     * 上传商品
     * @param entity
     */
    public void upGoods(ProductsEntity entity){
        ArrayList<ProductsEntity> list = new ArrayList<>();
        list.add(entity);
        upGoods(list);
    }

    /**
     * 转换需要上传的商品数据
     * @param upGoodsData
     */
    private void formatUpGoods(List<ProductsEntity> upGoodsData, int status){
        Observable.just(upGoodsData)
                .observeOn(Schedulers.io())
                .map(productsEntities -> LocaleUtil.formatUpDownStr(status, upGoodsData))
                .observeOn(Schedulers.io())
                .subscribe(s -> {
                    requestUpGoods(upGoodsData, s);
                });
    }

    /**
     * 上传商品
     * @param upGoodsData
     * @param param
     */
    private void requestUpGoods(List<ProductsEntity> upGoodsData, String param){
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
                        if (ResponseMgr.getStatus(s) != 1){
                            goodsView.onUpGoodsFailure();
                        }else {
                            goodsView.onUpGoodsSuccess(upGoodsData);
                        }
                    }
                });
    }

    /**
     * 加载品种
     */
    public void loadKindData(){
        goodsView.onStartLoadKinds();
        // 检查缓存
        if (AddGoodsData.INSTANCE.kindData != null){
            doParseKindData();
            return;
        }

        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getKind()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        goodsView.onLoadKindsFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            goodsView.onLoadKindsFailure();
                        }else {
                            AddGoodsData.INSTANCE.kindData = jsonObject;
                            doParseKindData();
                        }
                    }
                });
    }

    /**
     * 解析大类数据
     */
    private void doParseKindData(){
        JsonObject allKindData = AddGoodsData.INSTANCE.kindData;
        // 获取所有数据中的data字段
        JsonObject allKindDataObject = ResponseMgr.getData(allKindData);
        // 获取最大父类数据
        JsonArray largeTypeArray = allKindDataObject.get("0").getAsJsonArray();
        Gson gson = new Gson();
        // 解析所有大类的数据
        List<KindDataEntity> kindData = gson.fromJson(largeTypeArray,
                new TypeToken<List<KindDataEntity>>(){}.getType());

        KindDataEntity allKind = new KindDataEntity();
        allKind.setName("全部");
        allKind.setPid("-1");
        allKind.setId("-1");
        kindData.add(0, allKind);
        goodsView.onLoadKindsSuccess(kindData);
    }

    /**
     * 获取父类里面的所有小类
     * @param pid
     */
    public void getSmallType(String pid){
        JsonObject allKindData = AddGoodsData.INSTANCE.kindData;
        // 获取所有数据中的data字段
        JsonObject allKindDataObject = ResponseMgr.getData(allKindData);
        // 获取指定父类的所有数据
        JsonArray largeTypeArray = allKindDataObject.get(pid).getAsJsonArray();
        Gson gson = new Gson();
        // 解析所有大类的数据
        List<KindDataEntity> largeTypeArrayData = gson.fromJson(largeTypeArray,
                new TypeToken<List<KindDataEntity>>(){}.getType());
        // 回调数据
        goodsView.onLoadKindsSuccess(largeTypeArrayData);
    }
}
