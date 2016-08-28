package com.strangedog.weylen.mthc.activity.sales;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.activity.order.DoingOrderData;
import com.strangedog.weylen.mthc.adapter.SalesAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.SalesEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.view.ItemDividerDecoration;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
import com.strangedog.weylen.mthc.view.SpaceItemDecoration;
import com.strangedog.weylen.mthc.view.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SalesActivity extends BaseActivity implements SalesView{

    @Bind(R.id.salesPriceView) TextView mSalesPriceView;
    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;

    private SalesAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;
    private SalesPresenter salesPresenter;
    private boolean isAutoRefresh;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activity = this;

        init();

        salesPresenter = new SalesPresenter(this);
        salesPresenter.start();
    }

    void init(){
        adapter = new SalesAdapter(this);
        zWrapperAdapter = new ZWrapperAdapter(this, adapter);
        // 设置适配器
        mListRecyclerView.setAdapter(zWrapperAdapter);
        // 设置空视图
        emptyView.setText("无销售数据，点击刷新");
        emptyView.setOnClickListener(v-> salesPresenter.refresh());
        mListRecyclerView.setEmptyView(emptyView);
        // 设置刷新模式 设置必须在设置适配器之后
        mListRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mListRecyclerView.setArrowImageView(R.mipmap.abc_refresh_arrow);
        mListRecyclerView.addItemDecoration(new SpaceItemDecoration(DimensUtil.dp2px(this, 5)));
        // 设置刷新监听
        mListRecyclerView.setOnRefreshListener(new ListRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isAutoRefresh){
                    salesPresenter.refresh();
                }
                isAutoRefresh = false;
            }

            @Override
            public void onBottom() {
                if (SalesData.INSTANCE.isComplete) {
                    RecyclerViewStateUtils.setFooterViewState(activity, mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
                    return;
                }

                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mListRecyclerView);
                if (state == LoadingFooter.State.Loading) {
                    DebugUtil.d("AddProductsActivity onBottom the state is Loading, just wait..");
                    return;
                }

                if (state == LoadingFooter.State.Normal) {
                    RecyclerViewStateUtils.setFooterViewState(activity, mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.Loading, null);
                    salesPresenter.loadMore();
                } else if (state == LoadingFooter.State.TheEnd) {
                    RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, LoadingFooter.State.TheEnd);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sales, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void onStartRequest() {
        isAutoRefresh = true;
        mListRecyclerView.setRefreshing(true);
    }

    @Override
    public void onRequestFailure() {
        if (!isFinishing()){
            mListRecyclerView.refreshComplete();
            adapter.clear();
        }
    }

    @Override
    public void onRequestSuccess(List<SalesEntity> data, boolean isComplete) {
        mListRecyclerView.refreshComplete();
    }

    @Override
    public void onStartRefresh() {

    }

    @Override
    public void onStartLoadMore() {

    }

    @Override
    public void onLoadMoreFailure() {
        RecyclerViewStateUtils.setFooterViewState(activity, mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.NetWorkError,
                v -> {
                    RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, LoadingFooter.State.Loading);
                    salesPresenter.loadMore();
                });
    }

    @Override
    public void onLoadMoreSuccess(List<SalesEntity> data, boolean isComplete) {

    }
}
