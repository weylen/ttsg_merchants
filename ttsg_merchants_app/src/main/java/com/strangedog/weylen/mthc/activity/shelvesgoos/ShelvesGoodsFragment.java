package com.strangedog.weylen.mthc.activity.shelvesgoos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.activity.productsdetails.ProductsDetailsActivity;
import com.strangedog.weylen.mthc.adapter.ProductShelvesAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.KindDataEntity;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;
import com.strangedog.weylen.mthc.util.AnimatorUtil;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.view.ListBottomSheetDialog;
import com.strangedog.weylen.mthc.view.ListRecyclerView;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-08.
 * 商品-下架
 */
public class ShelvesGoodsFragment extends BaseFragment implements GoodsView {

    @Bind(R.id.recyclerView) ListRecyclerView mListRecyclerView;
    @Bind(R.id.emptyView) TextView emptyView;
    @Bind(R.id.containerView) View containerView;
    @Bind(R.id.text_checked) TextView mTextCheckedView;
    @Bind(R.id.bottom_layout) LinearLayout mBottomLayout;
    @Bind(R.id.checkedAllView) CheckBox checkBox;
    @Bind(R.id.text_currentType) TextView currentTypeView;

    private static final int STATUS = 2;

    private ProductShelvesAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;
    private SearchView searchView;
    private MenuItem actionFlowItem;
    private ShelvesPresenter presenter;

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

        emptyView.setText(R.string.NoShelvesProducts);
        emptyView.setOnClickListener(v-> presenter.refresh(true));

        presenter = new ShelvesPresenter(this);
        init();

        presenter.load(Constants.EMPTY_STR, STATUS, Constants.EMPTY_STR);
    }

    private void init() {
        // 创建列表适配器
        adapter = new ProductShelvesAdapter(getActivity());
        adapter.setItemViewClickListenerWrapper(itemViewClickListener);
        adapter.setOnCheckedChangeListener(count -> mTextCheckedView.setText("选中了" + count + "件商品"));
        zWrapperAdapter = new ZWrapperAdapter(getActivity(), adapter);
        zWrapperAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getActivity(), ProductsDetailsActivity.class);
            intent.putExtra(ProductsDetailsActivity.ENTITY_KEY, adapter.getItem(position));
            startActivity(intent);
        });
        // 设置适配器
        mListRecyclerView.setAdapter(zWrapperAdapter);
        // 设置空视图
        mListRecyclerView.setEmptyView(emptyView);
        mListRecyclerView.addItemDecoration(null);
        // 设置刷新模式 设置必须在设置适配器之后
        mListRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mListRecyclerView.setArrowImageView(R.mipmap.abc_arrow_down);

        // 设置刷新监听
        mListRecyclerView.setOnRefreshListener(new ListRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                presenter.refresh(false);
            }

            @Override
            public void onBottom() {
                if (isRefresh) {
                    return;
                }
                DebugUtil.d("ShelvesGoodsFragment-onBottom ShelvesGoodsData.INSTANCE.isComplete :" + ShelvesGoodsData.INSTANCE.isComplete);
                if (ShelvesGoodsData.INSTANCE.isComplete) {
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
    public void onUpGoodsBtnClick() {
        List<ProductsEntity> chooseData = adapter.getCheckedData();
        if (chooseData == null || chooseData.size() == 0){
            showSnakeView(containerView, "没有选中商品");
            return;
        }
        presenter.upGoods(chooseData);
    }

    @OnCheckedChanged(R.id.checkedAllView)
    public void onCheckedAllClick(boolean isChecked){
        adapter.setCheckAll(isChecked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_multi) {
            isMultiChooseShow = !isMultiChooseShow;
            item.setTitle(isMultiChooseShow ? "取消" : "多选");
            onMultiChooseClick();
            return true;
        }else if (item.getItemId() == R.id.action_flow){
            presenter.loadKindData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_products, menu);
        actionFlowItem = menu.findItem(R.id.action_flow);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                actionFlowItem.setEnabled(false);
                actionFlowItem.setIcon(R.mipmap.icon_menu_flow_disable);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                actionFlowItem.setEnabled(true);
                actionFlowItem.setIcon(R.mipmap.icon_more_24dp);
                return true;
            }
        });
        initSearchView();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_multi);
        item.setTitle(isMultiChooseShow ? "取消" : "多选");
    }

    private void initSearchView(){
        searchView.setQueryHint("搜索商品关键字");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SearchView.SearchAutoComplete textView = (SearchView.SearchAutoComplete) searchView
                .findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);
        setSearchViewTextCusor(textView);
//        View view = searchView.findViewById(R.id.search_plate);
//        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_input_bg));
    }

    private void setSearchViewTextCusor(SearchView.SearchAutoComplete view) {
        try {
            Class<?> mTextViewClass = view.getClass().getSuperclass()
                    .getSuperclass().getSuperclass().getSuperclass();
            Field mCursorDrawableRes = mTextViewClass.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(view, R.mipmap.icon_cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 执行搜索
    private void doMySearch(String query) {
        currentTypeView.setText("当前搜索条件：" + query);
        presenter.load(query, STATUS, Constants.EMPTY_STR);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    // 显示BottomSheet对话框
    private void showBottomSheetDialog(List<KindDataEntity> kindDataEntities) {
        ListBottomSheetDialog bottomSheetDialog = new ListBottomSheetDialog(getContext());
        bottomSheetDialog.setItemData(kindDataEntities);
        bottomSheetDialog.setItemClickListener(position -> {
            bottomSheetDialog.dismiss();
            KindDataEntity entity = kindDataEntities.get(position);
            // 大类
            if ("0".equalsIgnoreCase(entity.getPid())){
                presenter.getSmallType(entity.getId());
                // 全部
            }else if ("-1".equalsIgnoreCase(entity.getPid())){
                currentTypeView.setText("当前分类：全部");
                presenter.load(Constants.EMPTY_STR, STATUS, Constants.EMPTY_STR);
            }else {
                currentTypeView.setText("当前分类：" + entity.getName());
                presenter.load(Constants.EMPTY_STR, STATUS, entity.getId());
            }
        });
        bottomSheetDialog.show();
    }

    private ItemViewClickListenerWrapper itemViewClickListener = new ItemViewClickListenerWrapper() {
        @Override // 上架被点击
        public void onViewClick1(View view, int position) {
            DebugUtil.d("ShelvesGoodsFragment onViewClick2 position:" + position);
            ProductsEntity entity = adapter.getDataList().get(position);
            presenter.upGoods(entity);
        }
    };

    private void onMultiChooseClick() {
        adapter.setCheckBoxVisible(isMultiChooseShow);
        if (isMultiChooseShow){
            AnimatorUtil.scaleShow(mBottomLayout, null);
        }else {
            adapter.resetStatus();
            AnimatorUtil.scaleHide(mBottomLayout, new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {}
                @Override
                public void onAnimationEnd(View view) {
                    mBottomLayout.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationCancel(View view) {
                }
            });
        }
    }

    @Override
    public void onStartLoading() {
        showProgressDialog("获取数据中...");
    }

    @Override
    public void onLoadSuccess(List<ProductsEntity> listData, boolean isComplete) {
        DebugUtil.d("ShelvesGoodsFragment onLoadSuccess isComplete:" + isComplete);
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
            DebugUtil.d("ShelvesGoodsFragment onLoadSuccess isComplete:" + isComplete);
            dismissProgressDialog();
            RecyclerViewStateUtils.setFooterViewState(getActivity(), mListRecyclerView,
                    Constants.REQUEST_COUNT, isComplete ? LoadingFooter.State.TheEnd : LoadingFooter.State.Normal, null);
            adapter.addAll(listData);
        }
    }

    @Override
    public void onStartRefresh() {
        showProgressDialog("获取数据中");
    }

    @Override
    public void onStartUpGoods() {
        showProgressDialog("上架中...");
    }

    @Override
    public void onUpGoodsFailure() {
        if (isActive()) {
            dismissProgressDialog();
            showSnakeView(containerView, "上架失败，请重新操作");
        }
    }

    @Override
    public void onUpGoodsSuccess(List<ProductsEntity> upGoodsData) {
        if (isActive()) {
            dismissProgressDialog();
            showSnakeView(containerView, "上架成功");
            adapter.removeAll(upGoodsData);
            adapter.resetStatus();
            checkBox.setChecked(false);
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onStartLoadKinds() {
        showProgressDialog("获取分类数据中");
    }

    @Override
    public void onLoadKindsSuccess(List<KindDataEntity> dataEntities) {
        if (isActive()){
            dismissProgressDialog();
            showBottomSheetDialog(dataEntities);
        }
    }

    @Override
    public void onLoadKindsFailure() {
        if (isActive()){
            dismissProgressDialog();
            showSnakeView(containerView, "获取分类数据失败，请重新操作");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
