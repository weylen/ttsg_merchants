package com.strangedog.weylen.mthc.activity.addgoods;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.adapter.AddProductsAdapter;
import com.strangedog.weylen.mthc.adapter.LoadmoreListenerWrapper;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.view.ListRecyclerView;
import com.strangedog.weylen.mthc.view.ZRefreshView;

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
    @Bind(R.id.refreshView) ZRefreshView zRefreshView;

    private AddProductsAdapter adapter;
    private AddGoodsPresenter presenter;
    private SearchView searchView;
    private boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置可无添加商品信息
        emptyView.setText(R.string.NoAddProducts);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new AddGoodsPresenter(this);
        setPresenter(presenter);

        init();
    }

    private void init(){
        // 设置下拉刷新事件
        zRefreshView.setOnRefreshListener(()->presenter.refresh());
        // 创建列表适配器
        adapter = new AddProductsAdapter(LayoutInflater.from(this), null);
        // 设置数据监听器
        adapter.registerAdapterDataObserver(adapterDataObserver);
        // 设置适配器
        mListRecylerView.setAdapter(adapter, new LoadmoreListenerWrapper() {
            @Override
            public void onAfterLoadMoreRequested() {
                presenter.loadMore();
            }
        });
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
        //设置搜索栏底部横线的背景图
        View view = searchView.findViewById(R.id.search_plate);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_input_bg));
//        searchView.setSubmitButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    private void setSearchViewTextCusor(SearchView.SearchAutoComplete view) {
        try {
            Class<?> mTextViewClass = view.getClass().getSuperclass()
                    .getSuperclass().getSuperclass().getSuperclass();
            //mCursorDrawableRes光标图片Id的属性 这个属性是TextView的属性，所以要用view（SearchAutoComplete）
            //的父类（AutoCompleteTextView）的父  类( EditText）的父类(TextView)
            Field mCursorDrawableRes = mTextViewClass.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            //注意第一个参数持有这个属性的对象 光标必须是一张图片不能是颜色，因为光标有两张图片，第二张必须是一张图片 我这里是一张白色的图片
            mCursorDrawableRes.set(view, R.mipmap.icon_cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doMySearch(String query) {
        DebugUtil.d("AddProductsActivity onClick 搜索条件：" + query);
        presenter.onLoad(query);
//      searchView.onActionViewCollapsed()
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
//        if (item.getItemId() == R.id.action_search){
//            searchView.onActionViewExpanded();
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        presenter.onLoad("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        adapter.unregisterAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public void onStartLoading() {
        showProgressDialog("加载中...");
    }

    @Override
    public void onLoadFailure() {
        if (isActive()){
            dismissProgressDialog();
            zRefreshView.setRefreshing(false);
            adapter.setData(null);
            adapterDataObserver.onChanged();
        }
    }

    @Override
    public void onLoadSuccessful(List<ProductsEntity> data, boolean isComplete) {
        if (isActive()){
            dismissProgressDialog();
            mListRecylerView.setLoadFinishing(isComplete);
            zRefreshView.setRefreshing(false);
            adapter.setData(data);
        }
    }

    @Override
    public void onStartLoadmore() {

    }

    @Override
    public void onLoadmoreFailuer() {
        if (isActive()){
            dismissProgressDialog();
            showSnakeBar(containerView, "加载更多失败");
        }
    }

    @Override
    public void onLoadmoreSuccessful(List<ProductsEntity> data, boolean isComplete) {
        if (isActive()){
            dismissProgressDialog();
            mListRecylerView.setLoadFinishing(isComplete);
            zRefreshView.setRefreshing(false);
            adapter.addData(data);
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
    public void onUpLoadSuccess() {
        if (isActive()){
            dismissProgressDialog();
            showSnakeBar(containerView, "添加商品成功");
        }
    }

    @Override // 部分商品价格未设置或为0,添加失败
    public void onUploadDepartFailure() {}

    @Override
    public void setPresenter(AddGoodsPresenter presenter) {

    }

    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            int count = adapter.getItemCount();
            DebugUtil.d("AddProductsActivity onChanged count :" + count);
            if (count == 0){
                emptyView.setVisibility(View.VISIBLE);
            }else {
                emptyView.setVisibility(View.GONE);
            }
        }
    };
}
