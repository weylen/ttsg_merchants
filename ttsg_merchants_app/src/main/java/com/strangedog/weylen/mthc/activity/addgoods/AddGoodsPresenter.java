package com.strangedog.weylen.mthc.activity.addgoods;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.strangedog.weylen.mthc.BasePresenter;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;

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
        // 保存当前搜索关键字
        AddGoodsData.INSTANCE.keyword = keyword;
        addGoodsView.onStartLoading();
        getRemoteData(keyword == null ? "" : keyword, 1);
    }

    /**
     * 刷新
     */
    public void refresh(){
        getRemoteData(AddGoodsData.INSTANCE.keyword, 1);
    }

    /**
     * 加载更多
     */
    public void loadMore(){
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
                .subscribe(s -> uploadProducts(s));

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
            object.addProperty("buyPrice", entity.getBuyPrice());
            object.addProperty("salePrice", entity.getSalePrice());
            object.addProperty("stock", entity.getStock());
            object.addProperty("promote", entity.getPromote());
            object.addProperty("begin", entity.getBegin());
            object.addProperty("end", entity.getEnd());
            object.addProperty("info", entity.getInfo());
            array.add(object);
        }
        DebugUtil.d("AddGoodsPresenter-toJson 参数:" + array.toString());
        return array.toString();
    }

    /**
     * 上传选择产品
     * @param uploadInfo
     */
    private void uploadProducts(String uploadInfo){
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
                        DebugUtil.d("获取异常" + e.getMessage());
                        addGoodsView.onUploadFailure();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("SellingGoodsPresenter uploadProducts onNext s:" + s);
                        switch (ResponseMgr.getStatus(s)){
                            case 1:
                                addGoodsView.onUpLoadSuccess();
                                break;
                            case 2:
                                addGoodsView.onUploadDepartFailure();
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
}
