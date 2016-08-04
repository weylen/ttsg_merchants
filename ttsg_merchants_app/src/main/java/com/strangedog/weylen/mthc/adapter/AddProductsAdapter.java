package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
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
import com.strangedog.weylen.mthc.iinter.OnCheckedChangeListener;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 * 添加商品适配器
 */
public class AddProductsAdapter extends ListBaseAdapter<ProductsEntity> {

    private SparseBooleanArray checkedStatus = new SparseBooleanArray();
    private List<ProductsEntity> checkedData = new ArrayList<>();
    private OnCheckedChangeListener onCheckedChangeListener;
    private boolean isSelectAll;
    private int checkedCount = 0;
    private LayoutInflater inflate;
    public AddProductsAdapter(Context context){
        inflate = LayoutInflater.from(context);
    }

    @Override
    public AddProductsAdapter.A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate.inflate(R.layout.item_add_product, parent, false);
        AddProductsAdapter.A holder = new A(view);
        return holder;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    /**
     * 设置选中所有的商品
     * @param isSelectAll true 选中 false不选中
     */
    public void selectAll(boolean isSelectAll){
        this.isSelectAll = isSelectAll;
        checkedData = isSelectAll ? getDataList() : null;
        checkedStatus.clear();
        notifyDataSetChanged();
        checkedCount = isSelectAll ? getDataList().size() : 0;
        onCheckedChange();
    }

    /**
     * 重置所有的状态
     */
    public void resetStatus(){
        if (checkedData != null){
            checkedData.clear();
        }
        checkedStatus.clear();
        isSelectAll = false;
        checkedCount = 0;
        onCheckedChange();
    }

    /**
     * 获取选择的数据列表 可能为null
     * @return
     */
    public List<ProductsEntity> getCheckedData() {
        return checkedData;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        A holder = (A) viewHolder;

        ProductsEntity entity = getItem(position);
        // 选中
        holder.itemCheckedView.setOnClickListener((v)-> {
            boolean isChecked = holder.itemCheckedView.isChecked();
            // 保存选择状态
            checkedStatus.put(position, isChecked);
            if (checkedData == null){
                checkedData = new ArrayList<>();
            }
            // 添加或移除商品
            if (isChecked){
                checkedCount++;
                checkedData.add(entity);
            }else {
                checkedCount--;
                checkedData.remove(entity);
            }
            onCheckedChange();
        });
        // 设置选中效果
        holder.itemCheckedView.setChecked(checkedStatus.get(position, isSelectAll));

        holder.titleView.setText(entity.getName());
        holder.unitView.setText(entity.getStandard());
        String promotePrice = entity.getPromote(); // 促销价
        // 是否有促销信息
        if (LocaleUtil.hasPromotion(promotePrice)){
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

    private void onCheckedChange(){
        if (onCheckedChangeListener != null){
            onCheckedChangeListener.onCheckedChange(checkedCount);
        }
    }

    public static class A extends RecyclerView.ViewHolder {
        @Bind(R.id.itemChecked) CheckBox itemCheckedView;
        @Bind(R.id.itemTitle) TextView titleView;
        @Bind(R.id.itemUnit) TextView unitView;
        @Bind(R.id.itemPrice) TextView priceView;
        @Bind(R.id.itemPromotion) TextView promotionView;
        @Bind(R.id.itemPromotionPrice) TextView promotionPriceView;
        @Bind(R.id.itemImage) ImageView imageView;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
