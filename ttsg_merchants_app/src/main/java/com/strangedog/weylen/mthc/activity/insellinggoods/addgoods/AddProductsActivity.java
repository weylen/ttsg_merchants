package com.strangedog.weylen.mthc.activity.insellinggoods.addgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.rey.material.widget.Button;
import com.rey.material.widget.RadioButton;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.ProductsDetailsActivity;
import com.strangedog.weylen.mthc.adapter.AddProductsAdapter;
import com.strangedog.weylen.mthc.entity.AddProductsEntity;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.view.ItemDividerDecoration;
import com.strangedog.weylen.mthc.view.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 添加商品
 */
public class AddProductsActivity extends BaseActivity implements AddGoodsView{

    @Bind(R.id.recyclerView)
    ZRecyclerView mRecyclerView;
    @Bind(R.id.checkedAllView) RadioButton mCheckedAllView;
    @Bind(R.id.addProductsView) Button mAddProductsView;

    private AddProductsAdapter adapter;
    private AddGoodsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        int divider = getResources().getColor(R.color.divider);
        mRecyclerView.addItemDecoration(new ItemDividerDecoration().setSize(DimensUtil.dp2px(this, 0.3f)).setColor(divider));

        adapter = new AddProductsAdapter(LayoutInflater.from(this), initData());
        adapter.setItemClickListener((o, position) -> startActivity(new Intent(AddProductsActivity.this, ProductsDetailsActivity.class)));
        mRecyclerView.setAdapter(adapter);

        presenter = new AddGoodsPresenter(this);
        setPresenter(presenter);
    }
    private List<AddProductsEntity> initData() {
        List<AddProductsEntity> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(new AddProductsEntity());
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_products, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onLoad("");
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
        return false;
    }

    @Override
    public void setPresenter(AddGoodsPresenter presenter) {

    }
}
