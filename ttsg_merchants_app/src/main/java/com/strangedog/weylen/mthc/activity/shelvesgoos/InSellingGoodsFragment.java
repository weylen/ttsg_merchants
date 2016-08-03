package com.strangedog.weylen.mthc.activity.shelvesgoos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.rey.material.widget.Button;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.adapter.ProductInTheSaleAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-08.
 * 商品-下架
 */
public class InSellingGoodsFragment extends BaseFragment implements GoodsView {

    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;
    @Bind(R.id.containerView) View containerView;
    @Bind(R.id.text_checked) TextView mTextCheckedView;
    @Bind(R.id.bottom_layout) LinearLayout mBottomLayout;
    @Bind(R.id.upDownGoodsBtn) Button downGoodsBtn;

    private static final int STATUS = 1;

    private ProductInTheSaleAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;

    private InSellingPresenter presenter;

    private boolean isRefresh;
    private boolean isMultiChooseShow; // 多选是否打开

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int layoutId() {
        return R.layout.layout_product_updown;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        emptyView.setText(R.string.NoInsellingGoods);
        emptyView.setOnClickListener(v-> presenter.refresh());

        downGoodsBtn.setText(R.string.DownGoods);

        presenter = new InSellingPresenter(this);
        init();

        presenter.load(Constants.EMPTY_STR, STATUS);
    }

    private void init() {
        // 创建列表适配器
        adapter = new ProductInTheSaleAdapter(getActivity());
        adapter.setItemViewClickListenerWrapper(itemViewClickListener);
        adapter.setOnCheckedChangeListener(count -> mTextCheckedView.setText("选中了" + count + "件商品"));
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
                if (InSellingData.INSTANCE.isComplete) {
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


    @OnClick(R.id.upDownGoodsBtn)
    public void onDownGoodsBtnClick() {
        List<ProductsEntity> chooseData = adapter.getCheckedData();
        if (chooseData == null || chooseData.size() == 0){
            showSnakeView(containerView, "没有选中商品");
            return;
        }
        presenter.downGoods(chooseData);
    }

    @OnCheckedChanged(R.id.checkedAllView)
    public void onCheckedAllClick(boolean isChecked){
        adapter.setCheckAll(isChecked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_multi) {
            item.setTitle(isMultiChooseShow ? "多选" : "取消");
            isMultiChooseShow = !isMultiChooseShow;
            onMultiChooseClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private ItemViewClickListenerWrapper itemViewClickListener = new ItemViewClickListenerWrapper() {
        @Override // 下架被点击
        public void onViewClick1(View view, int position) {
            DebugUtil.d("ShelvesGoodsFragment onViewClick2 position:" + position);
            ProductsEntity entity = adapter.getDataList().get(position);
            presenter.downGoods(entity);
        }
    };

    private void onMultiChooseClick() {
        adapter.setCheckBoxVisible(isMultiChooseShow);
        if (isMultiChooseShow){
            mBottomLayout.setVisibility(View.VISIBLE);
        }else {
            adapter.resetStatus();
            mBottomLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStartLoading() {
        showProgressDialog("获取数据中...");
    }

    @Override
    public void onLoadSuccess(List<ProductsEntity> listData, boolean isComplete) {
        DebugUtil.d("InSellingFragment onLoadSuccess isComplete:" + isComplete);

        if (isActive()) {
            isRefresh = false;
            mListRecyclerView.refreshComplete();
            dismissProgressDialog();

            RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView,
                    Constants.REQUEST_COUNT, isComplete ? LoadingFooter.State.TheEnd : LoadingFooter.State.Normal, null);

            adapter.setDataList(listData);
            mListRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onLoadFailure() {
        if (isActive()) {
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
        if (isActive()) {
            dismissProgressDialog();
            RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView,
                    Constants.REQUEST_COUNT, LoadingFooter.State.NetWorkError, v -> presenter.loadMore());
        }
    }

    @Override
    public void onLoadMoreSuccess(List<ProductsEntity> listData, boolean isComplete) {
        if (isActive()) {
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
    public void onStartUpGoods() {
        showProgressDialog("上架中...");
    }

    @Override
    public void onUpGoodsFailure() {
        if (isActive()) {
            dismissProgressDialog();
            showSnakeView(containerView, "下架失败，请重新操作");
        }
    }

    @Override
    public void onUpGoodsSuccess(List<ProductsEntity> upGoodsData) {
        if (isActive()) {
            dismissProgressDialog();
            showSnakeView(containerView, "下架成功");
            adapter.removeAll(upGoodsData);
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
