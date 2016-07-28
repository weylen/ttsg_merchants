package com.strangedog.weylen.mthc;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.adapter.ProductInTheSaleAdapter;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.util.AppPrams;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.view.SpaceItemDecoration;
import com.strangedog.weylen.mthc.view.ZEmptyViewHelper;
import com.strangedog.weylen.mthc.view.ZRecyclerView;
import com.strangedog.weylen.mthc.view.ZRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-09.
 * 商品搜索
 */
public class SearchProductActivity extends BaseActivity {

    @Bind(R.id.searchView) EditText mSearchView;
    @Bind(R.id.recyclerView) ZRecyclerView mRecyclerView;
    @Bind(R.id.refreshView) ZRefreshView mRefreshView;

    private ProductInTheSaleAdapter adapter;
    private ZEmptyViewHelper emptyViewHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimensUtil.dp2px(this, AppPrams.DIVIDER), 1));

        // 初始化空视图
        emptyViewHelper = new ZEmptyViewHelper(getLayoutInflater(), mRefreshView, (FrameLayout)findViewById(R.id.containerView));
        emptyViewHelper.setOnEmptyViewClickListener(emptyViewClickListener);
        emptyViewHelper.setEmptyText("");

        adapter = new ProductInTheSaleAdapter(LayoutInflater.from(this), null);
        mRecyclerView.setAdapter(adapter);

        mRefreshView.setEnabled(false);
    }

    @OnClick(R.id.action_search)
    void onActionSearch(){
        String keyword = mSearchView.getText().toString();
        refresh();
        emptyViewHelper.setEmptyText("没有找到对应的商品");
    }

    @OnClick(R.id.action_back)
    void onActionBack(){
        finish();
    }

    private List<ProductsEntity> initData(){
        List<ProductsEntity> data = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            data.add(new ProductsEntity());
        }
        return data;
    }

    private ZEmptyViewHelper.OnEmptyViewClickListener emptyViewClickListener = ()->{};

    private void refresh(){
        showProgressDialog("搜索中...");
        new Handler().postDelayed(()->{
            dismissProgressDialog();
            adapter.setData(initData());
            mRefreshView.setRefreshing(false);
        }, 3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
