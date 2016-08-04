package com.strangedog.weylen.mthc.activity.order;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.adapter.OrderAdapter;
import com.strangedog.weylen.mthc.entity.OrderEntity;
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
 * Created by Administrator on 2016-07-02.
 */
public class CompleteOrderFragment extends BaseFragment {

    @Bind(R.id.recyclerView) ZRecyclerView mRecyclerView;
    @Bind(R.id.refreshView) ZRefreshView mRefreshView;

    private OrderAdapter adapter;
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
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimensUtil.dp2px(getContext(), 5)));

        // 初始化空视图
        emptyViewHelper = new ZEmptyViewHelper(getLayoutInflater(), mRefreshView,
                (FrameLayout)view.findViewById(R.id.containerView));
        emptyViewHelper.setOnEmptyViewClickListener(emptyViewClickListener);

        adapter = new OrderAdapter(LayoutInflater.from(getActivity()), null);
        mRecyclerView.setAdapter(adapter);

        mRefreshView.setOnRefreshListener(()->refresh());
    }

    private List<OrderEntity> initData(){
        List<OrderEntity> data = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            data.add(new OrderEntity());
        }
        return data;
    }

    private ZEmptyViewHelper.OnEmptyViewClickListener emptyViewClickListener = ()->refresh();

    private void refresh(){
        showToast("点击了空视图");
        showProgressDialog("刷新中...");
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
