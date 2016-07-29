package com.strangedog.weylen.mthc.activity.insellinggoods.addgoods;

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
import com.strangedog.weylen.mthc.util.SessionUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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

    public void addProducts(List<ProductsEntity> data){
        addGoodsView.onStartUpload();
        Observable.just(data)
                .observeOn(Schedulers.io())
                .map(productsEntities -> toJson(productsEntities))
                .observeOn(Schedulers.io())
                .subscribe(s -> uploadProducts(s));

    }

    private String toJson(List<ProductsEntity> data){
        JsonArray array = new JsonArray();
        for (ProductsEntity entity : data){
            JsonObject object = new JsonObject();
            object.addProperty("id", entity.getId());
            object.addProperty("buyPrice", "3");
            object.addProperty("salePrice", "3");
            object.addProperty("stock", "0");
            object.addProperty("promote", entity.getPromote());
            object.addProperty("begin", entity.getBegin());
            object.addProperty("end", entity.getEnd());
            object.addProperty("info", entity.getInfo());
            array.add(object);
        }
        DebugUtil.d("AddGoodsPresenter-toJson 参数:" + array.toString());
        return array.toString();
    }

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

    private void parse(JsonObject s){
        Gson gson = new Gson();
        List<ProductsEntity> data = gson.fromJson(s.get("data").getAsJsonArray(), new TypeToken<List<ProductsEntity>>(){}.getType());
        int maxPage = s.get("maxPage").getAsInt();
        int pageNum = s.get("pageNum").getAsInt();
        if (pageNum > 1){
            addGoodsView.onLoadmoreSuccessful(data, maxPage == pageNum);
        }else{
            addGoodsView.onLoadSuccessful(data, maxPage == pageNum);
        }
    }
}
