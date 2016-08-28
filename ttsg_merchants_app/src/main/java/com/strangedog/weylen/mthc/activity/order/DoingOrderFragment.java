package com.strangedog.weylen.mthc.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.activity.orderdetails.OrderDetailsActivity;
import com.strangedog.weylen.mthc.adapter.DoingOrderAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.OrderDetailsEntity;
import com.strangedog.weylen.mthc.entity.OrderDetailsProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
import com.strangedog.weylen.mthc.view.SpaceItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 进行中的订单
 */
public class DoingOrderFragment extends BaseFragment implements OrderView{

    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;
    @Bind(R.id.containerView) View containerView;

    private DoingOrderAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;
    private boolean isRefresh;
    private boolean isAutoRefresh;

    private DoingOrderPresenter presenter;

    @Override
    public int layoutId() {
        return R.layout.layout_generic_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        emptyView.setText("没有订单，点击刷新");
        emptyView.setOnClickListener(v-> presenter.refresh(true));

        presenter = new DoingOrderPresenter(this);
        init();
    }

    private void init() {
        // 创建列表适配器
        adapter = new DoingOrderAdapter(getActivity());
        adapter.setItemViewClickListenerWrapper(new ItemViewClickListenerWrapper() {
            @Override// 确认接单
            public void onViewClick1(View view, int position) {
                String orderId = adapter.getItem(position).getOrderId();
                showDialog("确认接单？", orderId, 7, position);
            }

            @Override// 确认送达
            public void onViewClick2(View view, int position) {
                String orderId = adapter.getItem(position).getOrderId();
                showDialog("确认送达？", orderId, 8, position);
            }
        });

        zWrapperAdapter = new ZWrapperAdapter(getActivity(), adapter);
        // 设置适配器
        mListRecyclerView.setAdapter(zWrapperAdapter);
        adapter.clear();
        // 设置空视图
        mListRecyclerView.setEmptyView(emptyView);
        // 设置刷新模式 设置必须在设置适配器之后
        mListRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mListRecyclerView.setArrowImageView(R.mipmap.abc_refresh_arrow);
        mListRecyclerView.addItemDecoration(new SpaceItemDecoration(DimensUtil.dp2px(getContext(), 10)));
        zWrapperAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
            intent.putExtra(OrderDetailsActivity.ORDER_KEY, adapter.getItem(position).getOrderId());
            startActivity(intent);
        });
        // 设置刷新监听
        mListRecyclerView.setOnRefreshListener(new ListRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isAutoRefresh){
                    isRefresh = true;
                    presenter.refresh(false);
                }
                isAutoRefresh = false;
            }

            @Override
            public void onBottom() {
                if (DoingOrderData.INSTANCE.isComplete) {
                    RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
                    return;
                }

                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mListRecyclerView);
                if (state == LoadingFooter.State.Loading) {
                    DebugUtil.d("AddProductsActivity onBottom the state is Loading, just wait..");
                    return;
                }

                if (state == LoadingFooter.State.Normal) {
                    RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.Loading, null);
                    presenter.loadMore();
                } else if (state == LoadingFooter.State.TheEnd) {
                    RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, LoadingFooter.State.TheEnd);
                }
            }
        });
    }

    private void showDialog(String message, String orderId, int status, int position){
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    presenter.alertOrderStatus(orderId, status, position);
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 加载订单列表
        presenter.startLoad();
    }

    private void resetRefreshState(){
        mListRecyclerView.setRefreshing(false);
        mListRecyclerView.refreshComplete();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgressDialog();
    }

    @Override
    public void onStartLoading() {
        isAutoRefresh = true;
        mListRecyclerView.setRefreshing(true);
    }

    @Override
    public void onLoadFailure() {
        if (isActive()){
            resetRefreshState();
            mListRecyclerView.refreshComplete();
        }
    }

    @Override
    public void onLoadSuccess(List<OrderDetailsEntity> orderEntityList, boolean isComplete) {
        if (isActive()){
            resetRefreshState();
            adapter.setDataList(orderEntityList);
            RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, isComplete ?  LoadingFooter.State.TheEnd :
                    LoadingFooter.State.Normal);
        }
    }

    @Override
    public void onStartLoadMore() {

    }

    @Override
    public void onLoadMoreFailure() {
        if (isActive()){
            dismissProgressDialog();
            RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.NetWorkError,
                    v -> {
                        RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, LoadingFooter.State.Loading);
                        presenter.loadMore();
                    });
        }
    }

    @Override
    public void onLoadMoreSuccess(List<OrderDetailsEntity> orderEntityList, boolean isComplete) {
        if (isActive()){
            adapter.addAll(orderEntityList);
            RecyclerViewStateUtils.setFooterViewState(mListRecyclerView, isComplete ?  LoadingFooter.State.TheEnd :
                    LoadingFooter.State.Normal);
        }
    }

    @Override
    public void onStartRefresh() {

    }

    @Override
    public void onReceiveOrder(int position) {

    }

    @Override
    public boolean isActive() {
        return isAdded() && !isDetached();
    }

    @Override
    public void onStartAlertStatus() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onAlertStatusFailure(int position, int status) {
        if (isActive()){
            dismissProgressDialog();
            if (status == 7){ // 确定接单
                showSnakeView(containerView, "接单失败，请重试");
            }else if (status == 8){
                showSnakeView(containerView, "确认送达失败，请重试");
            }
        }
    }

    @Override
    public void onAlertStatusSuccess(int position, int status) {
        if (isActive()){
            dismissProgressDialog();
            if (status == 7){ // 确定接单
                showSnakeView(containerView, "接单成功");
                adapter.getItem(position);
            }else if (status == 8){
                showSnakeView(containerView, "确认送达成功");
            }

            OrderDetailsEntity entity = adapter.getItem(position);
            OrderDetailsProductsEntity productsEntity = entity.getProducts().get(0);
            productsEntity.setStauts(String.valueOf(status));
            adapter.notifyItemChanged(position);
        }
    }
}
