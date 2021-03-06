package com.strangedog.weylen.mthc.activity.addgoods;

import com.strangedog.weylen.mthc.BaseView;
import com.strangedog.weylen.mthc.entity.KindDataEntity;
import com.strangedog.weylen.mthc.entity.ProductsEntity;

import java.util.List;

/**
 * Created by weylen on 2016-07-28.
 */
public interface AddGoodsView extends BaseView<AddGoodsPresenter>{

    /**
     * 开始加载数据
     */
    void onStartLoading();

    /**
     * 加载失败
     */
    void onLoadFailure();

    /**
     * 加载成功
     * @param data 数据
     * @param isComplete 数据是否获取完毕
     */
    void onLoadSuccessful(List<ProductsEntity> data, boolean isComplete);

    /**
     * 开始加载更多
     */
    void onStartLoadmore();

    /**
     * 加载更多失败
     */
    void onLoadmoreFailuer();

    /**
     * 加载更多成功
     * @param data 当前加载更多的数据
     * @param isComplete 数据是否加载完毕
     */
    void onLoadmoreSuccessful(List<ProductsEntity> data, boolean isComplete);

    /**
     * 是否存活
     * @return
     */
    boolean isActive();

    /**
     * 开始添加商品
     */
    void onStartUpload();

    void onUploadFailure();

    void onUpLoadSuccess(List<ProductsEntity> uploadData);

    /**
     * 开始加载品种
     */
    void onStartLoadKind();

    /**
     * 加载品种数据失败
     */
    void onLoadKindFailure();

    /**
     * 加载品种数据成功
     */
    void onLoadKindSuccess(List<KindDataEntity> kindData);
}
