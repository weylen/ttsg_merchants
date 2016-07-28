package com.strangedog.weylen.mthc.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.adapter.ProductInTheSaleAdapter;
import com.strangedog.weylen.mthc.adapter.ProductShelvesAdapter;
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

/**
 * Created by weylen on 2016-07-08.
 * 商品--在售
 */
public class F_Products_Shelves extends BaseFragment{

    @Bind(R.id.recyclerView) ZRecyclerView mRecyclerView;
    @Bind(R.id.refreshView) ZRefreshView mRefreshView;

    private ProductShelvesAdapter adapter;
    private ZEmptyViewHelper emptyViewHelper;

    @Override
    public int layoutId() {
        return R.layout.fragment_swipe_recyler;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimensUtil.dp2px(getActivity(), AppPrams.DIVIDER), 1));

        // 初始化空视图
        emptyViewHelper = new ZEmptyViewHelper(getLayoutInflater(), mRefreshView,
                (FrameLayout)view.findViewById(R.id.containerView));
        emptyViewHelper.setOnEmptyViewClickListener(emptyViewClickListener);

        adapter = new ProductShelvesAdapter(LayoutInflater.from(getActivity()), initData());
        mRecyclerView.setAdapter(adapter);

        mRefreshView.setOnRefreshListener(()->refresh());
        mRefreshView.setEnabled(false);
    }

    private List<ProductsEntity> initData(){
        List<ProductsEntity> data = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            data.add(new ProductsEntity());
        }
        return data;
    }

    private ZEmptyViewHelper.OnEmptyViewClickListener emptyViewClickListener = ()->refresh();

    private void refresh(){
        showProgressDialog("刷新中...");
        new Handler().postDelayed(()->{
            dismissProgressDialog();
            mRefreshView.setRefreshing(false);
        }, 3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
