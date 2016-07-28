package com.strangedog.weylen.mthc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.rey.material.widget.EditText;
import com.strangedog.weylen.mtch.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by weylen on 2016-07-10.
 */
public class ProductsDetailsActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.image) ImageView mImage;
    @Bind(R.id.itemProductsName) EditText mItemProductsName;
    @Bind(R.id.itemPrice) EditText mItemPrice;
    @Bind(R.id.itemUnit) EditText mItemUnit;
    @Bind(R.id.itemInventory) EditText mItemInventory;
    @Bind(R.id.itemPromotion) EditText mItemPromotion;

    private boolean isEditable; // 是否为编辑模式

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enableOrDisableView();
    }

    private void enableOrDisableView(){
        if (isEditable){
            mToolbar.setTitle("商品编辑");
            mItemProductsName.setEnabled(true);
            mItemPrice.setEnabled(true);
            mItemInventory.setEnabled(true);
            mItemUnit.setEnabled(true);
            mItemPromotion.setEnabled(true);
        }else {
            mToolbar.setTitle("商品详情");
            mItemProductsName.setEnabled(false);
            mItemPrice.setEnabled(false);
            mItemInventory.setEnabled(false);
            mItemUnit.setEnabled(false);
            mItemPromotion.setEnabled(false);
        }
    }

    private MenuItem menuItemEdit;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products_details, menu);
        menuItemEdit = menu.findItem(R.id.action_edit);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isEditable == false){
                finish();
            }else {
                // 是编辑模式则进行取消操作 然后改变图标
                mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);
                menuItemEdit.setIcon(R.mipmap.ic_edit_white);
                isEditable = false;
                enableOrDisableView();
            }

            return true;
        }else if (item.getItemId() == R.id.action_edit){
            if (isEditable){ // 如果之前是可编辑状态
                // 则进行保存操作 然后改变图标
                mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);
                item.setIcon(R.mipmap.ic_edit_white);
            }else{
                // 不是编辑状态 则进入编辑状态
                mToolbar.setNavigationIcon(R.mipmap.ic_clear_white);
                item.setIcon(R.mipmap.ic_done_white);
            }
            isEditable = !isEditable;
            enableOrDisableView();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
