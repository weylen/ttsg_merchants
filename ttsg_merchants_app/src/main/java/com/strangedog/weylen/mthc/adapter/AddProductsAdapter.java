package com.strangedog.weylen.mthc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.AddProductsEntity;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.iinter.BaseItemViewClickListener;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2016-07-02.
 * 添加商品适配器
 */
public class AddProductsAdapter extends WrapperAdapterData<AddProductsEntity, AddProductsAdapter.A> {

    private ItemClickListener itemClickListener;
    private BaseItemViewClickListener itemViewClickListener;

    public AddProductsAdapter(LayoutInflater inflater, List<AddProductsEntity> data) {
        super(inflater, data);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemViewClickListener(BaseItemViewClickListener itemViewClickListener) {
        this.itemViewClickListener = itemViewClickListener;
    }

    @Override
    public AddProductsAdapter.A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_add_product, parent, false);
        AddProductsAdapter.A holder = new A(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(AddProductsAdapter.A holder, int position) {
        AddProductsEntity entity = getItem(position);
        RxView.clicks(holder.itemView).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    if (itemClickListener != null){
                        itemClickListener.onItemClicked(entity, position);
                    }
                });
        // 选中
        holder.itemCheckedView.setOnClickListener((v)->{
            if (itemViewClickListener != null){
                itemViewClickListener.onViewClick1(holder.itemCheckedView, position);
            }
        });

        //TODO 设置数据
    }


    public static class A extends RecyclerView.ViewHolder {
        @Bind(R.id.itemChecked) CheckBox itemCheckedView;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
