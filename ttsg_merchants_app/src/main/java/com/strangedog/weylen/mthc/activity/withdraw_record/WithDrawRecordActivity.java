package com.strangedog.weylen.mthc.activity.withdraw_record;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.activity.stock.StockData;
import com.strangedog.weylen.mthc.activity.stock.StockPresenter;
import com.strangedog.weylen.mthc.activity.stock.StockView;
import com.strangedog.weylen.mthc.adapter.StockAdapter;
import com.strangedog.weylen.mthc.adapter.WithdrawRecordAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.StockEntity;
import com.strangedog.weylen.mthc.entity.WithdrawRecordEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.KeyboardUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
import com.strangedog.weylen.mthc.view.SpaceItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-08-29.
 */
public class WithDrawRecordActivity extends BaseActivity implements RecordView{

    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;

    private Activity activity;
    private WithdrawRecordAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;

    private RecordPresenter presenter;
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
        presenter = new RecordPresenter(this);
        presenter.start();
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
        adapter = new WithdrawRecordAdapter(this);
        zWrapperAdapter = new ZWrapperAdapter(this, adapter);
        // 设置适配器
        mListRecyclerView.setAdapter(zWrapperAdapter);
        // 设置空视图
        emptyView.setText("暂无转出记录");
        emptyView.setOnClickListener(v-> presenter.refresh());
        mListRecyclerView.setEmptyView(emptyView);
        // 设置刷新模式 设置必须在设置适配器之后
        mListRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mListRecyclerView.setArrowImageView(R.mipmap.abc_refresh_arrow);
        mListRecyclerView.addItemDecoration(new SpaceItemDecoration(1));
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
                if (RecordData.INSTANCE.isComplete) {
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
        showSnakeBar(mListRecyclerView, "获取数据失败");
        adapter.clear();
    }

    @Override
    public void onListSuccess(List<WithdrawRecordEntity> data, boolean isComplete) {
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
    public void onLoadMoreSuccess(List<WithdrawRecordEntity> data, boolean isComplete) {
        adapter.addAll(data);
        RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, isComplete ?  LoadingFooter.State.TheEnd :
                LoadingFooter.State.Normal);
    }
}
