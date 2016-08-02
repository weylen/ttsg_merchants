package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListener;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2016-07-02.
 * 在售商品适配器
 */
public class ProductInTheSaleAdapter extends ListBaseAdapter<ProductsEntity>{

    private ItemViewClickListenerWrapper itemViewClickListener;
    private LayoutInflater layoutInflater;

    public ProductInTheSaleAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    public void setItemViewClickListener(ItemViewClickListenerWrapper itemViewClickListener) {
        this.itemViewClickListener = itemViewClickListener;
    }

    @Override
    public ProductInTheSaleAdapter.A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_products_inthesale, parent, false);
        ProductInTheSaleAdapter.A holder = new A(view, itemViewClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        A holder = (A) viewHolder;
        ProductsEntity entity = getItem(position);

        holder.titleView.setText(entity.getName());
        holder.unitView.setText(entity.getStandard());
        String promotePrice = entity.getPromote(); // 促销价
        if (!TextUtils.isEmpty(promotePrice)){
            holder.priceView.setText("￥"+promotePrice);
            String info = entity.getBegin() + "~" + entity.getEnd() + " " + entity.getInfo();
            holder.promotionView.setText(info);
            holder.promotionPriceView.setText("￥"+entity.getSalePrice());
            holder.promotionPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        }else{
            holder.priceView.setText("￥" + entity.getSalePrice());
        }

        String imgPath = entity.getImgPath();
        Glide.with(holder.priceView.getContext())
                .load(Constants.BASE_URL + imgPath.split(",")[0])
                .fitCenter()
                .placeholder(R.mipmap.img_default)
                .crossFade()
                .dontAnimate()
                .error(R.mipmap.img_default)
                .into(holder.imageView);
    }


    public static class A extends RecyclerView.ViewHolder {
        @Bind(R.id.action_edit) TextView actionEditView;
        @Bind(R.id.action_shelves) TextView actionShelvesView;

        @Bind(R.id.itemTitle) android.widget.TextView titleView;
        @Bind(R.id.itemUnit) android.widget.TextView unitView;
        @Bind(R.id.itemPrice) android.widget.TextView priceView;
        @Bind(R.id.itemPromotion) android.widget.TextView promotionView;
        @Bind(R.id.itemPromotionPrice) android.widget.TextView promotionPriceView;
        @Bind(R.id.itemImage) ImageView imageView;

        public A(View itemView, ItemViewClickListenerWrapper itemViewClickListenerWrapper) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // 编辑
            actionEditView.setOnClickListener((v)->{
                if (itemViewClickListenerWrapper != null){
                    itemViewClickListenerWrapper.onViewClick1(actionEditView, getLayoutPosition());
                }
            });

            // 下架
            actionShelvesView.setOnClickListener((v)->{
                if (itemViewClickListenerWrapper != null){
                    itemViewClickListenerWrapper.onViewClick2(actionShelvesView, getLayoutPosition());
                }
            });
        }
    }
}
