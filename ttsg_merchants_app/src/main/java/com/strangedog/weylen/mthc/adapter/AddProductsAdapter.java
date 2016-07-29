package com.strangedog.weylen.mthc.adapter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.rey.material.widget.CheckBox;
import com.strangedog.weylen.mtch.R;
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
public class AddProductsAdapter extends WrapperAdapterData<ProductsEntity, AddProductsAdapter.A> {

    private ItemClickListener itemClickListener;
    private BaseItemViewClickListener itemViewClickListener;

    public AddProductsAdapter(LayoutInflater inflater, List<ProductsEntity> data) {
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
        ProductsEntity entity = getItem(position);
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
        holder.titleView.setText(entity.getName());
        holder.unitView.setText(entity.getStandard());
        String promotePrice = entity.getPromote(); // 促销价
        if (!TextUtils.isEmpty(promotePrice)){
            holder.priceView.setText(promotePrice);
            String info = entity.getBegin() + "~" + entity.getEnd() + " " + entity.getInfo();
            holder.promotionView.setText(info);
            holder.promotionPriceView.setText(entity.getSalePrice());
            holder.promotionPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        }else{
            holder.priceView.setText("￥" + entity.getSalePrice());
        }
    }


    public static class A extends RecyclerView.ViewHolder {
        @Bind(R.id.itemChecked) CheckBox itemCheckedView;
        @Bind(R.id.itemTitle) TextView titleView;
        @Bind(R.id.itemUnit) TextView unitView;
        @Bind(R.id.itemPrice) TextView priceView;
        @Bind(R.id.itemPromotion) TextView promotionView;
        @Bind(R.id.itemPromotionPrice) TextView promotionPriceView;


        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
