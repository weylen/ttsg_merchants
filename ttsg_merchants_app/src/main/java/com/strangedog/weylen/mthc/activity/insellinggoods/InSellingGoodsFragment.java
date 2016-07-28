package com.strangedog.weylen.mthc.activity.insellinggoods;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.rey.material.app.Dialog;
import com.rey.material.app.SimpleDialog;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.adapter.OrderAdapter;
import com.strangedog.weylen.mthc.adapter.ProductInTheSaleAdapter;
import com.strangedog.weylen.mthc.entity.OrderEntity;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.iinter.BaseItemViewClickListener;
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
public class InSellingGoodsFragment extends BaseFragment implements SellingGoodsView{

    @Bind(R.id.recyclerView) ZRecyclerView mRecyclerView;
    @Bind(R.id.refreshView) ZRefreshView mRefreshView;

    private ProductInTheSaleAdapter adapter;
    private ZEmptyViewHelper emptyViewHelper;

    private SellingGoodsPresenter presenter;

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

        adapter = new ProductInTheSaleAdapter(LayoutInflater.from(getActivity()), null);
        // 设置列表item视图被点击的监听
        adapter.setItemViewClickListener(itemViewClickListener);

        mRecyclerView.setAdapter(adapter);

        mRefreshView.setOnRefreshListener(()->refresh());
        mRefreshView.setEnabled(false);

        presenter = new SellingGoodsPresenter(this);
        setPresenter(presenter);
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
    public void onResume() {
        super.onResume();
        presenter.onLoad("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private BaseItemViewClickListener itemViewClickListener = new BaseItemViewClickListener(){
        @Override // 编辑被点击
        public void onViewClick1(View view, int position) {

        }

        @Override // 下架被点击
        public void onViewClick2(View view, int position) {
            showShelvesDialog(position);
        }
    };

    /**
     * 下架对话框
     */
    private void showShelvesDialog(int position){
        SimpleDialog dialog = new SimpleDialog(getActivity());
//        dialog.title("提示");
        dialog.message("确定要下架当前商品？")
                .neutralAction("取消")
                .negativeActionClickListener(v -> {
                        dialog.dismiss();
                })
                .positiveAction("确定")
                .positiveActionClickListener(v->{
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onStartLoading() {
        showProgressDialog("加载中...");
    }

    @Override
    public void onLoadFailure() {
        dismissProgressDialog();
    }

    @Override
    public void onLoadSuccessful(List<ProductsEntity> data, boolean isComplete) {
        dismissProgressDialog();
    }

    @Override
    public void onStartLoadmore() {

    }

    @Override
    public void onLoadmoreFailuer() {
        dismissProgressDialog();
    }

    @Override
    public void onLoadmoreSuccessful(List<ProductsEntity> data, boolean isComplete) {
        dismissProgressDialog();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(SellingGoodsPresenter presenter) {

    }
}
