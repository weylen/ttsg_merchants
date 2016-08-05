package com.strangedog.weylen.mthc.activity.addgoods;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.adapter.AddProductsAdapter;
import com.strangedog.weylen.mthc.adapter.ZWrapperAdapter;
import com.strangedog.weylen.mthc.entity.KindDataEntity;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
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
 * 添加商品
 */
public class AddProductsActivity extends BaseActivity implements AddGoodsView{

    @Bind(R.id.recyclerView) ListRecyclerView mListRecylerView;
    @Bind(R.id.containerView) View containerView;
    @Bind(R.id.emptyView) TextView emptyView;
    @Bind(R.id.text_checked) TextView checkCountView;

    private AddProductsAdapter adapter;
    private ZWrapperAdapter zWrapperAdapter;

    private AddGoodsPresenter presenter;
    private SearchView searchView;
    private boolean isActive;
    private boolean isRefresh; // 是否正在刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置可无添加商品信息
        emptyView.setText(R.string.NoAddProducts);

        emptyView.setOnClickListener(v-> presenter.refresh(true));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new AddGoodsPresenter(this);
        setPresenter(presenter);

        init();

        presenter.onLoad(Constants.EMPTY_STR, Constants.EMPTY_STR);
    }

    private void init(){
        // 创建列表适配器
        adapter = new AddProductsAdapter(this);
        adapter.setOnCheckedChangeListener(count->{checkCountView.setText("选中了" + count +"件商品");});
        zWrapperAdapter = new ZWrapperAdapter(this, adapter);
        // 设置适配器
        mListRecylerView.setAdapter(zWrapperAdapter);
        mListRecylerView.addItemDecoration(null);
        // 设置空视图
        mListRecylerView.setEmptyView(emptyView);
        // 设置刷新模式 设置必须在设置适配器之后
        mListRecylerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mListRecylerView.setArrowImageView(R.mipmap.icon_arrow_down);
        // 设置刷新监听
        mListRecylerView.setOnRefreshListener(new ListRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                presenter.refresh(false);
            }

            @Override
            public void onBottom() {
                if (isRefresh){return;}

                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mListRecylerView);
                if(state == LoadingFooter.State.Loading) {
                    DebugUtil.d("AddProductsActivity onBotton the state is Loading, just wait..");
                    return;
                }

                if (state == LoadingFooter.State.Normal){
                    RecyclerViewStateUtils.setFooterViewState(AddProductsActivity.this, mListRecylerView, Constants.REQUEST_COUNT, LoadingFooter.State.Loading, null);
                    presenter.loadMore();
                }else if (state == LoadingFooter.State.TheEnd){
                    RecyclerViewStateUtils.setFooterViewState(AddProductsActivity.this, mListRecylerView, Constants.REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
                }
            }
        });
    }

    // 显示BottomSheet对话框
    private void showBottomSheetDialog(List<KindDataEntity> kindDataEntities) {
        ListBottomSheetDialog bottomSheetDialog = new ListBottomSheetDialog(this);
        bottomSheetDialog.setItemData(kindDataEntities);
        bottomSheetDialog.setItemClickListener(position -> {
            bottomSheetDialog.dismiss();
            KindDataEntity entity = kindDataEntities.get(position);
            // 大类
            if ("0".equalsIgnoreCase(entity.getPid())){
                presenter.getSmallType(entity.getId());
                // 全部
            }else if ("-1".equalsIgnoreCase(entity.getPid())){
                presenter.onLoad(Constants.EMPTY_STR, Constants.EMPTY_STR);
            }else {
                presenter.onLoad(Constants.EMPTY_STR, entity.getId());
            }
        });
        bottomSheetDialog.show();
    }

    @OnClick(R.id.addProductsView)
    public void addProductsClick(){
        List<ProductsEntity> data = adapter.getCheckedData();
        if (data == null || data.size() == 0){
            showSnakeBar(containerView, "没有选择商品");
            return;
        }
        presenter.addProducts(data);
    }

    @OnCheckedChanged(R.id.checkedAllView)
    public void onCheckedAllClick(boolean isChecked){
        adapter.selectAll(isChecked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_products, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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
        View view = searchView.findViewById(R.id.search_plate);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_input_bg));
        return super.onCreateOptionsMenu(menu);
    }

    private void setSearchViewTextCusor(SearchView.SearchAutoComplete view) {
        try {
            Class<?> mTextViewClass = view.getClass().getSuperclass()
                    .getSuperclass().getSuperclass().getSuperclass();
            Field mCursorDrawableRes = mTextViewClass.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            //注意第一个参数持有这个属性的对象 光标必须是一张图片不能是颜色，因为光标有两张图片，第二张必须是一张图片 我这里是一张白色的图片
            mCursorDrawableRes.set(view, R.mipmap.icon_cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 执行搜索
    private void doMySearch(String query) {
        presenter.onLoad(query, Constants.EMPTY_STR);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_flow){
            presenter.loadKindData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
        AddGoodsData.INSTANCE.reset();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStartLoading() {
        showProgressDialog("加载中...");
    }

    @Override
    public void onLoadFailure() {
        if (isActive()){
            isRefresh = false;
            mListRecylerView.refreshComplete();
            dismissProgressDialog();
            adapter.clear();
        }
    }

    @Override
    public void onLoadSuccessful(List<ProductsEntity> data, boolean isComplete) {
        if (isActive()){
            isRefresh = false;
            mListRecylerView.refreshComplete();
            dismissProgressDialog();


            RecyclerViewStateUtils.setFooterViewState(AddProductsActivity.this, mListRecylerView,
                    Constants.REQUEST_COUNT, isComplete ? LoadingFooter.State.TheEnd : LoadingFooter.State.Normal, null);
            adapter.setDataList(data);
            mListRecylerView.scrollToPosition(0);
        }
    }

    @Override
    public void onStartLoadmore() {

    }

    @Override
    public void onLoadmoreFailuer() {
        if (isActive()){
            dismissProgressDialog();
            RecyclerViewStateUtils.setFooterViewState(AddProductsActivity.this, mListRecylerView,
                    Constants.REQUEST_COUNT, LoadingFooter.State.NetWorkError, v -> presenter.loadMore());
        }
    }

    @Override
    public void onLoadmoreSuccessful(List<ProductsEntity> data, boolean isComplete) {
        if (isActive()){
            dismissProgressDialog();
            RecyclerViewStateUtils.setFooterViewState(AddProductsActivity.this, mListRecylerView,
                    Constants.REQUEST_COUNT, isComplete ? LoadingFooter.State.TheEnd : LoadingFooter.State.Normal, null);

            adapter.addAll(data);
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onStartUpload() {
        showProgressDialog("提交数据中");
    }

    @Override
    public void onUploadFailure() {
        if (isActive()){
            dismissProgressDialog();
            showSnakeBar(containerView, "添加数据失败，请重新操作");
        }
    }

    @Override
    public void onUpLoadSuccess(List<ProductsEntity> uploadData) {
        if (isActive()){
            dismissProgressDialog();
            showSnakeBar(containerView, "添加商品成功");
            // 移除已经上传的商品
            adapter.getDataList().removeAll(uploadData);
            adapter.resetStatus();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStartLoadKind() {
        showProgressDialog("加载品类中...");
    }

    @Override
    public void onLoadKindFailure() {
        if (isActive()){
            dismissProgressDialog();
            showSnakeBar(containerView, "加载品类失败，请重新操作");
        }
    }

    @Override
    public void onLoadKindSuccess(List<KindDataEntity> kindData) {
        if (isActive()){
            dismissProgressDialog();
            if (kindData == null || kindData.size() == 0){
                showSnakeBar(containerView, "获取分类数据失败，请重新操作");
                return;
            }
            showBottomSheetDialog(kindData);
        }
    }

    @Override
    public void setPresenter(AddGoodsPresenter presenter) {}
}
