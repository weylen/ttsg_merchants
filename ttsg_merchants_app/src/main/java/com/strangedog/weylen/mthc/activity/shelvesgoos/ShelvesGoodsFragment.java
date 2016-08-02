package com.strangedog.weylen.mthc.activity.shelvesgoos;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.activity.insellinggoods.SellingGoodsPresenter;
import com.strangedog.weylen.mthc.adapter.ProductInTheSaleAdapter;
import com.strangedog.weylen.mthc.adapter.ProductShelvesAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;
import com.strangedog.weylen.mthc.util.AppPrams;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
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
 * 商品-下架
 */
public class ShelvesGoodsFragment extends BaseFragment implements ShelvesGoodsView{

    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;

    private static final int STATUS = 2;
    private ProductShelvesAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;

    private ShelvesGoodsPresenter presenter;

    private boolean isRefresh;

    @Override
    public int layoutId() {
        return R.layout.layout_generic_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        emptyView.setText(R.string.NoInsellingGoods);

        presenter = new ShelvesGoodsPresenter(this);
        init();

        presenter.load(Constants.EMPTY_STR, STATUS);
    }

    private void init(){
        // 创建列表适配器
        adapter = new ProductShelvesAdapter(getActivity());
        adapter.setItemViewClickListenerWrapper(itemViewClickListener);
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
                if (isRefresh){return;}

                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mListRecyclerView);
                if(state == LoadingFooter.State.Loading) {
                    DebugUtil.d("AddProductsActivity onBotton the state is Loading, just wait..");
                    return;
                }

                if (state == LoadingFooter.State.Normal){
                    RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.Loading, null);
                    presenter.loadMore();
                }else if (state == LoadingFooter.State.TheEnd){
                    RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView, Constants.REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private ItemViewClickListenerWrapper itemViewClickListener = new ItemViewClickListenerWrapper(){
        @Override // 编辑被点击
        public void onViewClick1(View view, int position) {

        }

        @Override // 上架被点击
        public void onViewClick2(View view, int position) {
        }
    };

    @Override
    public void onStartLoading() {
        showProgressDialog("获取数据中...");
    }

    @Override
    public void onLoadSuccess(List<ProductsEntity> listData, boolean isComplete) {
        if (isActive()){
            isRefresh = false;
            mListRecyclerView.refreshComplete();
            dismissProgressDialog();
            adapter.setDataList(listData);
        }
    }

    @Override
    public void onLoadFailure() {
        if (isActive()){
            isRefresh = false;
            mListRecyclerView.refreshComplete();
            dismissProgressDialog();
            adapter.clear();
        }
    }

    @Override
    public void onStartLoadMore() {

    }

    @Override
    public void onLoadMoreFailure() {
        if (isActive()){
            dismissProgressDialog();
            RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView,
                    Constants.REQUEST_COUNT, LoadingFooter.State.NetWorkError, v -> presenter.loadMore());
        }
    }

    @Override
    public void onLoadMoreSuccess(List<ProductsEntity> listData, boolean isComplete) {
        if (isActive()){
            dismissProgressDialog();
            RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView,
                    Constants.REQUEST_COUNT, isComplete ? LoadingFooter.State.TheEnd : LoadingFooter.State.Normal, null);
            adapter.addAll(listData);
        }
    }

    @Override
    public void onStartRefresh() {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
