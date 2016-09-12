package com.strangedog.weylen.mthc.activity.promotion_goods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.activity.productsdetails.ProductsDetailsActivity;
import com.strangedog.weylen.mthc.activity.withdraw_record.RecordData;
import com.strangedog.weylen.mthc.activity.withdraw_record.RecordPresenter;
import com.strangedog.weylen.mthc.activity.withdraw_record.RecordView;
import com.strangedog.weylen.mthc.adapter.PromotionGoodsAdapter;
import com.strangedog.weylen.mthc.adapter.WithdrawRecordAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.entity.PromotionEntity;
import com.strangedog.weylen.mthc.entity.WithdrawRecordEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
import com.strangedog.weylen.mthc.view.SpaceItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhou on 2016/9/8.
 */
public class PromotionGoodsActivity extends BaseActivity implements PromotionView {
    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;

    private Activity activity;
    private PromotionGoodsAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;

    private PromotionPresenter presenter;
    private boolean isAutoRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.activity = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        presenter = new PromotionPresenter(this);
        presenter.start("", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    void init(){
        adapter = new PromotionGoodsAdapter(this);
        adapter.setItemViewClickListenerWrapper(new ItemViewClickListenerWrapper() {
            @Override
            public void onViewClick1(View view, int position) {
                new AlertDialog.Builder(PromotionGoodsActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要清除促销信息吗？")
                        .setNegativeButton("取消", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setPositiveButton("确定", (dialog, which) -> {
                            dialog.dismiss();
                            alertGoodsInfo(adapter.getDataList().get(position), position);
                        })
                        .show();
            }
        });
        zWrapperAdapter = new ZWrapperAdapter(this, adapter);
        // 设置适配器
        mListRecyclerView.setAdapter(zWrapperAdapter);
        // 设置空视图
        emptyView.setText("暂无促销商品");
        emptyView.setOnClickListener(v-> presenter.refresh());
        mListRecyclerView.setEmptyView(emptyView);
        // 设置刷新模式 设置必须在设置适配器之后
        mListRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mListRecyclerView.setArrowImageView(R.mipmap.abc_refresh_arrow);
        mListRecyclerView.addItemDecoration(new SpaceItemDecoration(1));
        zWrapperAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(PromotionGoodsActivity.this, ProductsDetailsActivity.class);
            intent.putExtra(ProductsDetailsActivity.ENTITY_KEY, adapter.getItem(position));
            startActivity(intent);
        });
        // 设置刷新监听
        mListRecyclerView.setOnRefreshListener(new ListRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isAutoRefresh){
                    presenter.refresh();
                }
                isAutoRefresh = false;
            }

            @Override
            public void onBottom() {
                if (PromotionData.INSTANCE.isComplete) {
                    RecyclerViewStateUtils.setFooterViewState(activity, mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
                    return;
                }

                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mListRecyclerView);
                if (state == LoadingFooter.State.Loading) {
                    return;
                }

                if (state == LoadingFooter.State.Normal) {
                    RecyclerViewStateUtils.setFooterViewState(activity, mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.Loading, null);
                    presenter.loadMore();
                } else if (state == LoadingFooter.State.TheEnd) {
                    RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, LoadingFooter.State.TheEnd);
                }
            }
        });
    }

    private void resetRefreshState(){
        mListRecyclerView.setRefreshing(false);
        mListRecyclerView.refreshComplete();
    }

    @Override
    public void onStartList() {
        isAutoRefresh = true;
        mListRecyclerView.setRefreshing(true);
    }

    @Override
    public void onListFailure() {
        resetRefreshState();
        adapter.clear();
    }

    @Override
    public void onListSuccess(List<ProductsEntity> data, boolean isComplete) {
        resetRefreshState();
        adapter.setDataList(data);
        RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, isComplete ?  LoadingFooter.State.TheEnd :
                LoadingFooter.State.Normal);
    }

    @Override
    public void onLoadMoreFailure() {
        showSnakeBar(mListRecyclerView, "加载更多失败");
        RecyclerViewStateUtils.setFooterViewState(this, mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.NetWorkError,
                v -> {
                    RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, LoadingFooter.State.Loading);
                    presenter.loadMore();
                });
    }

    @Override
    public void onLoadMoreSuccess(List<ProductsEntity> data, boolean isComplete) {
        adapter.addAll(data);
        RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, isComplete ?  LoadingFooter.State.TheEnd :
                LoadingFooter.State.Normal);
    }

    /**
     * 修改商品信息
     */
    private void alertGoodsInfo(ProductsEntity productsEntity, int position){
        showProgressDialog("清除中...");
        String param = map(productsEntity);
        DebugUtil.d("ProductsDetailsActivity 保存参数：" + param);
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .alertGoodsInfo(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        snake("请求失败，请检查网络");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            snake("清除成功");
                            adapter.getDataList().remove(position);
                            adapter.notifyItemRemoved(position);
                        }else {
                            snake("清除促销信息失败，请重试");
                        }
                    }
                });
    }

    private String map(ProductsEntity productsEntity){
        JsonArray array = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", productsEntity.getId());
        jsonObject.addProperty("salePrice", productsEntity.getSalePrice());
        jsonObject.addProperty("promote", "-1");
//        jsonObject.addProperty("begin", "");
//        jsonObject.addProperty("end", "");
        jsonObject.addProperty("info", "");
        jsonObject.addProperty("added", productsEntity.getStauts());
        array.add(jsonObject);
        return array.toString();
    }

    private void snake(String message){
        showSnakeBar(mListRecyclerView, message);
    }
}
