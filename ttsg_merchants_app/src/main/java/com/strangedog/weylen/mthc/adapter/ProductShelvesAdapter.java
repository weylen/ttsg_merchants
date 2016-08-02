package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2016-07-02.
 * 商品--下架商品
 */
public class ProductShelvesAdapter extends ListBaseAdapter<ProductsEntity> {

    private ItemViewClickListenerWrapper itemViewClickListenerWrapper;

    private LayoutInflater layoutInflater;

    public ProductShelvesAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    public void setItemViewClickListenerWrapper(ItemViewClickListenerWrapper itemViewClickListenerWrapper) {
        this.itemViewClickListenerWrapper = itemViewClickListenerWrapper;
    }

    @Override
    public ProductShelvesAdapter.A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_products_shelves, parent, false);
        ProductShelvesAdapter.A holder = new A(view, itemViewClickListenerWrapper);
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
        @Bind(R.id.action_resales) TextView actionResalesView;

        @Bind(R.id.itemTitle) android.widget.TextView titleView;
        @Bind(R.id.itemUnit) android.widget.TextView unitView;
        @Bind(R.id.itemPrice) android.widget.TextView priceView;
        @Bind(R.id.itemPromotion) android.widget.TextView promotionView;
        @Bind(R.id.itemPromotionPrice) android.widget.TextView promotionPriceView;
        @Bind(R.id.itemImage) ImageView imageView;

        public A(View itemView, ItemViewClickListenerWrapper itemViewClickListenerWrapper) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            actionEditView.setOnClickListener((v)->{
                if (itemViewClickListenerWrapper != null){
                    itemViewClickListenerWrapper.onViewClick1(v, getLayoutPosition());
                }
            });
            actionResalesView.setOnClickListener((v)->{
                if (itemViewClickListenerWrapper != null){
                    itemViewClickListenerWrapper.onViewClick2(v, getLayoutPosition());
                }
            });
        }
    }
}
