package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.entity.PromotionEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;
import com.strangedog.weylen.mthc.iinter.OnCheckedChangeListener;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 * 促销商品
 */
public class PromotionGoodsAdapter extends ListBaseAdapter<ProductsEntity> {

    private ItemViewClickListenerWrapper itemViewClickListenerWrapper;
    private OnCheckedChangeListener onCheckedChangeListener;

    private LayoutInflater layoutInflater;

    private boolean isVisible;
    private boolean isSelectAll;
    private int checkedCount;
    private Context context;

    private SparseBooleanArray checkedStatus = new SparseBooleanArray();
    private List<ProductsEntity> checkedData = new ArrayList<>();

    public PromotionGoodsAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setCheckBoxVisible(boolean isVisible){
        this.isVisible = isVisible;
        notifyDataSetChanged();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setCheckAll(boolean isSelectAll){
        this.isSelectAll = isSelectAll;
        if (isSelectAll){
            if (checkedData == null){
                checkedData = new ArrayList<>();
            }
            checkedData.clear();
            checkedData.addAll(getDataList());
        }else {
            checkedData.clear();
        }
        checkedStatus.clear();
        notifyDataSetChanged();
        checkedCount = isSelectAll ? getDataList().size() : 0;
        onCheckedChange();
    }

    /**
     * 获取选择的数据列表 可能为null
     * @return
     */
    public List<ProductsEntity> getCheckedData() {
        return checkedData;
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
        notifyDataSetChanged();
    }

    private void onCheckedChange(){
        if (onCheckedChangeListener != null){
            onCheckedChangeListener.onCheckedChange(checkedCount);
        }
    }

    public void setItemViewClickListenerWrapper(ItemViewClickListenerWrapper itemViewClickListenerWrapper) {
        this.itemViewClickListenerWrapper = itemViewClickListenerWrapper;
    }

    @Override
    public PromotionGoodsAdapter.A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_promotion_goods, parent, false);
        PromotionGoodsAdapter.A holder = new A(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        A holder = (A) viewHolder;
        ProductsEntity entity = getItem(position);

        holder.titleView.setText(entity.getName()); // 商品名字
        holder.unitView.setText(entity.getStandard()); // 单位
        holder.stockView.setText("库存：" + entity.getStock()); // 库存
        String promotePrice = entity.getPromote(); // 促销价
        if (LocaleUtil.hasPromotion(promotePrice)){
            holder.priceView.setText("￥"+promotePrice);
            String info = entity.getBegin() + "~" + entity.getEnd() + " " + (TextUtils.isEmpty(entity.getInfo())? "" : entity.getInfo());
            holder.promotionView.setText(info);
            holder.promotionPriceView.setText("￥"+entity.getSalePrice());
            holder.promotionPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //中划线
            holder.promotionView.setVisibility(View.VISIBLE);
        }else{
            holder.priceView.setText("￥" + entity.getSalePrice());
            holder.promotionView.setVisibility(View.GONE);
        }

        if (isVisible){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.clearPromotionView.setVisibility(View.GONE);
        }else {
            holder.checkBox.setVisibility(View.GONE);
            holder.clearPromotionView.setVisibility(View.VISIBLE);
        }
        // 设置选中效果
        holder.checkBox.setChecked(checkedStatus.get(position, isSelectAll));
        // 选中
        holder.checkBox.setOnClickListener((v)-> {
            boolean isChecked = holder.checkBox.isChecked();
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

        holder.clearPromotionView.setOnClickListener(v->{
            if (itemViewClickListenerWrapper != null){
                itemViewClickListenerWrapper.onViewClick1(v, position);
            }
        });


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
        @Bind(R.id.action_clearPromotion) TextView clearPromotionView;

        @Bind(R.id.itemTitle) android.widget.TextView titleView;
        @Bind(R.id.itemUnit) android.widget.TextView unitView;
        @Bind(R.id.itemPrice) android.widget.TextView priceView;
        @Bind(R.id.itemPromotion) android.widget.TextView promotionView;
        @Bind(R.id.itemPromotionPrice) android.widget.TextView promotionPriceView;
        @Bind(R.id.text_stock) android.widget.TextView stockView;
        @Bind(R.id.itemImage)
        ImageView imageView;
        @Bind(R.id.itemChecked)
        CheckBox checkBox;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
