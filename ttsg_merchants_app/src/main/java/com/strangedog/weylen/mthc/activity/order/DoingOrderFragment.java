package com.strangedog.weylen.mthc.activity.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.activity.shelvesgoos.ShelvesGoodsData;
import com.strangedog.weylen.mthc.activity.shelvesgoos.ShelvesPresenter;
import com.strangedog.weylen.mthc.adapter.DoingOrderAdapter;
import com.strangedog.weylen.mthc.adapter.OrderAdapter;
import com.strangedog.weylen.mthc.adapter.ProductShelvesAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.OrderEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
import com.strangedog.weylen.mthc.view.SpringView;
import com.strangedog.weylen.mthc.view.ZRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 */
public class DoingOrderFragment extends BaseFragment implements OrderView{

    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;
    @Bind(R.id.containerView) View containerView;

    private DoingOrderAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;
    private boolean isRefresh;

    private DoingOrderPresenter presenter;

    @Override
    public int layoutId() {
        return R.layout.layout_generic_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        emptyView.setText("没有订单，点击刷新");
        emptyView.setOnClickListener(v-> presenter.refresh());

        presenter = new DoingOrderPresenter(this);
        init();
    }

    private void init() {
        // 创建列表适配器
        adapter = new DoingOrderAdapter(getActivity(), null);
        zWrapperAdapter = new ZWrapperAdapter(getActivity(), adapter);
        // 设置适配器
        mListRecyclerView.setAdapter(zWrapperAdapter);
        // 设置空视图
        mListRecyclerView.setEmptyView(emptyView);
        // 设置刷新模式 设置必须在设置适配器之后
        mListRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mListRecyclerView.setArrowImageView(R.mipmap.icon_arrow_down);
        // 设置刷新监听
        mListRecyclerView.setOnRefreshListener(new ListRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                presenter.refresh();
            }

            @Override
            public void onBottom() {
                if (isRefresh) {
                    return;
                }
                if (DoingOrderData.INSTANCE.isComplete) {
                    RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
                    return;
                }

                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mListRecyclerView);
                if (state == LoadingFooter.State.Loading) {
                    DebugUtil.d("AddProductsActivity onBotton the state is Loading, just wait..");
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

    @Override
    public void onStartLoading() {

    }

    @Override
    public void onLoadFailure() {

    }

    @Override
    public void onLoadSuccess(List<OrderEntity> orderEntityList) {

    }

    @Override
    public void onStartLoadMore() {

    }

    @Override
    public void onLoadMoreFailure() {

    }

    @Override
    public void onLoadMoreSuccess(List<OrderEntity> orderEntityList) {

    }

    @Override
    public void onStartRefresh() {

    }

    @Override
    public void onReceiveOrder(int position) {

    }
}
