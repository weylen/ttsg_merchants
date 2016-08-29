package com.strangedog.weylen.mthc.activity.stock;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.activity.sales.SalesData;
import com.strangedog.weylen.mthc.adapter.SalesAdapter;
import com.strangedog.weylen.mthc.adapter.StockAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.StockEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.util.LocaleUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
import com.strangedog.weylen.mthc.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by weylen on 2016-08-29.
 */
public class StockActivity extends BaseActivity implements StockView{

    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;

    private Activity activity;
    private StockAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;

    private StockPresenter presenter;
    private boolean isAutoRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.activity = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new StockPresenter(this);

        init();
        adapter.setDataList(test());
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


    void init(){
        adapter = new StockAdapter(this);
        zWrapperAdapter = new ZWrapperAdapter(this, adapter);
        // 设置适配器
        mListRecyclerView.setAdapter(zWrapperAdapter);
        // 设置空视图
        emptyView.setText("无数据，点击刷新");
        emptyView.setOnClickListener(v-> presenter.refresh());
        mListRecyclerView.setEmptyView(emptyView);
        // 设置刷新模式 设置必须在设置适配器之后
        mListRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mListRecyclerView.setArrowImageView(R.mipmap.abc_refresh_arrow);
        mListRecyclerView.addItemDecoration(new SpaceItemDecoration(DimensUtil.dp2px(this, 5)));
        mListRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mListRecyclerView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
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
                if (StockData.INSTANCE.isComplete) {
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
                    presenter.loadMore();
                } else if (state == LoadingFooter.State.TheEnd) {
                    RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, LoadingFooter.State.TheEnd);
                }
            }
        });
    }

    private List<StockEntity> test(){
        List<StockEntity> data = new ArrayList<>();
        for (int i = 0; i < 50; i++){
            data.add(new StockEntity());
        }
        return data;
    }

    @Override
    public void onStartList() {

    }

    @Override
    public void onListFailure() {

    }

    @Override
    public void onListSuccess(List<StockEntity> data, boolean isComplete) {

    }

    @Override
    public void onLoadMoreFailure() {

    }

    @Override
    public void onLoadMoreSuccess(List<StockEntity> data, boolean isComplete) {

    }

    @Override
    public void onRefreshFailure() {

    }

    @Override
    public void onRefreshSuccess(List<StockEntity> data, boolean isComplete) {

    }
}
