package com.strangedog.weylen.mthc.activity.addgoods;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.BasePresenter;
import com.strangedog.weylen.mthc.entity.KindDataEntity;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
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
        DebugUtil.d("AddGoodsPresenter onLoad 执行");
        // 保存当前搜索关键字
        AddGoodsData.INSTANCE.keyword = keyword;
        addGoodsView.onStartLoading();
        getRemoteData(keyword == null ? "" : keyword, 1);
    }

    /**
     * 刷新
     */
    public void refresh(){
        DebugUtil.d("AddGoodsPresenter refresh 执行");
        getRemoteData(AddGoodsData.INSTANCE.keyword, 1);
    }

    /**
     * 加载更多
     */
    public void loadMore(){
        DebugUtil.d("AddGoodsPresenter loadMore 执行");
        getRemoteData(AddGoodsData.INSTANCE.keyword, AddGoodsData.INSTANCE.pageNum + 1);
    }

    /**
     * 添加商品
     * @param data
     */
    public void addProducts(List<ProductsEntity> data){
        addGoodsView.onStartUpload();
        Observable.just(data)
                .observeOn(Schedulers.io())
                .map(productsEntities -> toJson(productsEntities))
                .observeOn(Schedulers.io())
                .subscribe(s -> uploadProducts(data, s));
    }

    /**
     * 转换成json
     * @param data
     * @return
     */
    private String toJson(List<ProductsEntity> data){
        JsonArray array = new JsonArray();
        for (ProductsEntity entity : data){
            JsonObject object = new JsonObject();
            object.addProperty("id", entity.getId());
            object.addProperty("buyPrice", "0");
            object.addProperty("salePrice", "0");
            array.add(object);
        }
        return array.toString();
    }

    /**
     * 上传选择产品
     * @param uploadInfo 添加商品的信息
     * @param uploadData 上传的数据列表
     */
    private void uploadProducts(final List<ProductsEntity> uploadData, String uploadInfo){
        DebugUtil.d("AddGoodsPresenter-uploadProducts uploadInfo:" + uploadInfo);
        RetrofitFactory.getRetrofit()
                .create(HttpService.class)
                .addProducts(uploadInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        DebugUtil.d("uploadProducts-->" + e.getMessage());
                        addGoodsView.onUploadFailure();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("SellingGoodsPresenter uploadProducts onNext s:" + s);
                        switch (ResponseMgr.getStatus(s)){
                            case 1:
                                addGoodsView.onUpLoadSuccess(uploadData);
                                break;
                            default:
                                addGoodsView.onUploadFailure();
                                break;
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
            addGoodsView.onLoadmoreFailuer();
        }else{
            addGoodsView.onLoadFailure();
        }
    }

    /**
     * 获取产品列表
     * @param keyword
     * @param pageNum
     */
    private void getRemoteData(String keyword, int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getInSellingGoods(keyword, pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        DebugUtil.d("getRemoteData-->" + e.getMessage() + ",pageNum-->" + pageNum);
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
     * 解析json数据
     * @param s
     */
    private void parse(JsonObject s){
        Gson gson = new Gson();
        List<ProductsEntity> data = gson.fromJson(s.get("data").getAsJsonArray(), new TypeToken<List<ProductsEntity>>(){}.getType());
        int maxPage = s.get("maxPage").getAsInt();
        int pageNum = s.get("pageNum").getAsInt();
        // 保存当前页面
        AddGoodsData.INSTANCE.pageNum = pageNum;
        if (pageNum > 1){
            addGoodsView.onLoadmoreSuccessful(data, maxPage == pageNum);
        }else{
            addGoodsView.onLoadSuccessful(data, maxPage == pageNum);
        }
    }

    /**
     * 加载品种
     */
    public void loadKindData(){
        addGoodsView.onStartLoadKind();
        // 检查缓存
        if (AddGoodsData.INSTANCE.kindData != null){
            doParseData();
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
                        DebugUtil.d("AddGoodsPresenter loadKind onError-->" + e.getMessage());
                        addGoodsView.onLoadKindFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            addGoodsView.onLoadKindFailure();
                        }else {
                            AddGoodsData.INSTANCE.kindData = jsonObject;
                            doParseData();
                        }
                    }
                });
    }

    /**
     * 解析大类数据
     */
    private void doParseData(){
        JsonObject allKindData = AddGoodsData.INSTANCE.kindData;
        // 获取所有数据中的data字段
        JsonObject allKindDataObject = ResponseMgr.getData(allKindData);
        // 获取最大父类数据
        JsonArray largeTypeArray = allKindDataObject.get("0").getAsJsonArray();
        Gson gson = new Gson();
        // 解析所有大类的数据
        List<KindDataEntity> kindData = gson.fromJson(largeTypeArray,
                new TypeToken<List<KindDataEntity>>(){}.getType());
        addGoodsView.onLoadKindSuccess(kindData);
    }
}
