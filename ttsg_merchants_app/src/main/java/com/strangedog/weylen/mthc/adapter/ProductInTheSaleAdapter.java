package com.strangedog.weylen.mthc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.iinter.BaseItemViewClickListener;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2016-07-02.
 * 在手商品适配器
 */
public class ProductInTheSaleAdapter extends WrapperAdapterData<ProductsEntity, ProductInTheSaleAdapter.A> {

    private ItemClickListener itemClickListener;
    private BaseItemViewClickListener itemViewClickListener;

    public ProductInTheSaleAdapter(LayoutInflater inflater, List<ProductsEntity> data) {
        super(inflater, data);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemViewClickListener(BaseItemViewClickListener itemViewClickListener) {
        this.itemViewClickListener = itemViewClickListener;
    }

    @Override
    public ProductInTheSaleAdapter.A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_products_inthesale, parent, false);
        ProductInTheSaleAdapter.A holder = new A(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(ProductInTheSaleAdapter.A holder, int position) {
        ProductsEntity entity = getItem(position);
        RxView.clicks(holder.itemView).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    if (itemClickListener != null){
                        itemClickListener.onItemClicked(entity, position);
                    }
                });
        // 编辑
        holder.actionEditView.setOnClickListener((v)->{
            if (itemViewClickListener != null){
                itemViewClickListener.onViewClick1(holder.actionEditView, position);
            }
        });

        // 下架
        holder.actionShelvesView.setOnClickListener((v)->{
            if (itemViewClickListener != null){
                itemViewClickListener.onViewClick2(holder.actionShelvesView, position);
            }
        });
        //TODO 设置数据
    }


    public static class A extends RecyclerView.ViewHolder {
        @Bind(R.id.action_edit) TextView actionEditView;
        @Bind(R.id.action_shelves) TextView actionShelvesView;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
