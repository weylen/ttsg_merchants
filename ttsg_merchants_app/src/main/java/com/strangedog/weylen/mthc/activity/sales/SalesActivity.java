package com.strangedog.weylen.mthc.activity.sales;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.adapter.SalesAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.SalesEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.LocaleUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
import com.strangedog.weylen.mthc.view.SaleQueryDialog;
import com.strangedog.weylen.mthc.view.SpaceItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SalesActivity extends BaseActivity implements SalesView{

    @Bind(R.id.salesPriceView) TextView mSalesPriceView;
    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;
    @Bind(R.id.text_time) TextView timeView;

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
        salesPresenter.start(Constants.EMPTY_STR, Constants.EMPTY_STR);

        timeView.setText("时间段：全部");
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
        mListRecyclerView.addItemDecoration(new SpaceItemDecoration(1));
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
        }else if (item.getItemId() == R.id.action_search){
            showQueryDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showQueryDialog(){
        SaleQueryDialog dialog = new SaleQueryDialog(this);
        dialog.setOnDataListener((startTime, endTime) -> {
            timeView.setText("时间段：" + startTime +" ~ " + endTime);
            salesPresenter.start(startTime + Constants.TIME_START, endTime + Constants.TIME_END);
        });
        dialog.show();
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
            mSalesPriceView.setText(Constants.EMPTY_STR);
        }
    }

    @Override
    public void onRequestSuccess(List<SalesEntity> data, String total, boolean isComplete) {
        if (!isFinishing()){
            mListRecyclerView.refreshComplete();
            adapter.setDataList(data);
            // 设置销售总额
            mSalesPriceView.setText(LocaleUtil.formatMoney(total));
            RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, isComplete ? LoadingFooter.State.TheEnd : LoadingFooter.State.Normal);
        }
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
    public void onLoadMoreSuccess(List<SalesEntity> data, String total, boolean isComplete) {
        if (!isFinishing()){
            mListRecyclerView.refreshComplete();
            adapter.addAll(data);

            RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, isComplete ? LoadingFooter.State.TheEnd : LoadingFooter.State.Normal);
        }
    }
}
