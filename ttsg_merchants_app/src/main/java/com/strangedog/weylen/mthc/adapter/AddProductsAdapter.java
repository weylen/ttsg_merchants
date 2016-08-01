package com.strangedog.weylen.mthc.adapter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 * 添加商品适配器
 */
public class AddProductsAdapter extends WrapperAdapterData<ProductsEntity, AddProductsAdapter.A> {

    private ItemClickListener itemClickListener;
    private SparseBooleanArray checkedStatus = new SparseBooleanArray();
    private List<ProductsEntity> checkedData = new ArrayList<>();
    private boolean isSelectAll;

    public AddProductsAdapter(LayoutInflater inflater, ItemClickListener itemClickListener) {
        super(inflater, null);
        setItemClickListener(itemClickListener);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public AddProductsAdapter.A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_add_product, parent, false);
        AddProductsAdapter.A holder = new A(view, itemClickListener);
        return holder;
    }

    /**
     * 设置选中所有的商品
     * @param isSelectAll true 选中 false不选中
     */
    public void selectAll(boolean isSelectAll){
        this.isSelectAll = isSelectAll;
        checkedData = isSelectAll ? getData() : null;
        checkedStatus.clear();
        notifyDataSetChanged();
    }

    /**
     * 重置所有的状态
     */
    public void resetStatus(){
        checkedData.clear();
        checkedStatus.clear();
        isSelectAll = false;
    }

    /**
     * 获取选择的数据列表 可能为null
     * @return
     */
    public List<ProductsEntity> getCheckedData() {
        return checkedData;
    }

    @Override
    public void onBindViewHolder(AddProductsAdapter.A holder, int position) {
        ProductsEntity entity = getItem(position);
        // 选中
        holder.itemCheckedView.setOnClickListener((v)-> {
            boolean isChecked = holder.itemCheckedView.isChecked();
            // 保存选择状态
            checkedStatus.put(position, isChecked);
            // 添加或移除商品
            if (isChecked){
                checkedData.add(entity);
            }else {
                checkedData.remove(entity);
            }
        });
        // 设置选中效果
        holder.itemCheckedView.setChecked(checkedStatus.get(position, isSelectAll));

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
        @Bind(R.id.itemChecked) CheckBox itemCheckedView;
        @Bind(R.id.itemTitle) TextView titleView;
        @Bind(R.id.itemUnit) TextView unitView;
        @Bind(R.id.itemPrice) TextView priceView;
        @Bind(R.id.itemPromotion) TextView promotionView;
        @Bind(R.id.itemPromotionPrice) TextView promotionPriceView;
        @Bind(R.id.itemImage) ImageView imageView;

        public A(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if (itemClickListener != null){
                    itemClickListener.onItemClicked(getLayoutPosition());
                }
            });
        }
    }
}
