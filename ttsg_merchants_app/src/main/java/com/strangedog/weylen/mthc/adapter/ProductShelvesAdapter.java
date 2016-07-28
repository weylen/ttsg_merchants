package com.strangedog.weylen.mthc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2016-07-02.
 * 商品--下架商品
 */
public class ProductShelvesAdapter extends WrapperAdapterData<ProductsEntity, ProductShelvesAdapter.A> {

    private ItemClickListener itemClickListener;

    public ProductShelvesAdapter(LayoutInflater inflater, List<ProductsEntity> data) {
        super(inflater, data);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ProductShelvesAdapter.A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_products_shelves, parent, false);
        ProductShelvesAdapter.A holder = new A(view);
        holder.actionEditView.setOnClickListener((v)->{});
        holder.actionResalesView.setOnClickListener((v)->{});

        return holder;
    }

    @Override
    public void onBindViewHolder(ProductShelvesAdapter.A holder, int position) {
        ProductsEntity entity = getItem(position);
        RxView.clicks(holder.itemView).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    if (itemClickListener != null){
                        itemClickListener.onItemClicked(entity, position);
                    }
                });

        //TODO 设置数据
    }


    public static class A extends RecyclerView.ViewHolder {
        @Bind(R.id.action_edit) TextView actionEditView;
        @Bind(R.id.action_resales) TextView actionResalesView;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
