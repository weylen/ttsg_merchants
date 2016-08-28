package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.hash.HashingOutputStream;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.AccountEntity;
import com.strangedog.weylen.mthc.entity.OrderDetailsEntity;
import com.strangedog.weylen.mthc.entity.OrderDetailsProductsEntity;
import com.strangedog.weylen.mthc.entity.OrderEntity;
import com.strangedog.weylen.mthc.entity.OrderProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;
import com.strangedog.weylen.mthc.view.OrderProductsDetailsView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 */
public class DoingOrderAdapter extends ListBaseAdapter<OrderDetailsEntity>{

    private LayoutInflater mLayoutInflater;
    private ItemViewClickListenerWrapper itemViewClickListenerWrapper;

    public DoingOrderAdapter(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_order, parent, false);
        return new A(view);
    }

    public void setItemViewClickListenerWrapper(ItemViewClickListenerWrapper itemViewClickListenerWrapper) {
        this.itemViewClickListenerWrapper = itemViewClickListenerWrapper;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder aHolder, int position) {
        A holder = (A) aHolder;
        OrderDetailsEntity orderEntity = getItem(position);
        List<OrderDetailsProductsEntity> productsEntities = orderEntity.getProducts();
        OrderDetailsProductsEntity productsEntity = productsEntities.get(0);
        // 订单状态
        String status = productsEntity.getStauts();

        holder.mOrderStatusView.setText(Constants.ORDER_PARAM.get(status));
        holder.mOrderTimeView.setText(productsEntity.getDate());
        holder.mOrderPriceView.setText("￥" + orderEntity.getTotal());
        int totalCount = holder.mOrderContentView.setDataAndNotify2(productsEntities);
        // 总数量
        holder.mOrderCountView.setText(String.format("共%d件商品", totalCount));
        // 收货人
        holder.mOrderContactsView.setText("联系人：" + productsEntity.getFname() +"    " + productsEntity.getTele());
        // 联系地址
        holder.mOrderAddressView.setText("地址：" + productsEntity.getAddr());
        // 备注
        String note = productsEntity.getNote();
        holder.mOrderNoteView.setText("备注：" + (TextUtils.isEmpty(note) ? "无":note));

        holder.actionLayoutView.setVisibility(View.VISIBLE);
        // 3：已支付未发货 6：支付确认中 7：商家已结单 8：商家已送达
        if ("1".equalsIgnoreCase(status) || "8".equalsIgnoreCase(status)){ //
            holder.actionLayoutView.setVisibility(View.GONE);
        }else if ("3".equalsIgnoreCase(status)){
            holder.actionConfirmGoodsView.setVisibility(View.VISIBLE);
            holder.actionDeliveryView.setVisibility(View.GONE);
        }else if ("7".equalsIgnoreCase(status)){
            holder.actionConfirmGoodsView.setVisibility(View.GONE);
            holder.actionDeliveryView.setVisibility(View.VISIBLE);
        }

        // 点击事件 // 确认接单
        holder.actionConfirmGoodsView.setOnClickListener(v -> {
            if (itemViewClickListenerWrapper != null){
                itemViewClickListenerWrapper.onViewClick1(holder.itemView, position);
            }
        }); // 确认送达
        holder.actionDeliveryView.setOnClickListener(v -> {
            if (itemViewClickListenerWrapper != null){
                itemViewClickListenerWrapper.onViewClick2(holder.itemView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  mDataList == null ? 0 : mDataList.size();
    }

    public static class A extends RecyclerView.ViewHolder{
        @Bind(R.id.orderStatusView)  TextView mOrderStatusView;
        @Bind(R.id.orderTimeView)  TextView mOrderTimeView;
        @Bind(R.id.orderContentView) OrderProductsDetailsView mOrderContentView;
        @Bind(R.id.orderPriceView)  TextView mOrderPriceView;
        @Bind(R.id.orderAddressView) TextView mOrderAddressView;
        @Bind(R.id.orderContactsView) TextView mOrderContactsView;
        @Bind(R.id.orderCountView) TextView mOrderCountView;
        @Bind(R.id.orderNoteView) TextView mOrderNoteView;
        @Bind(R.id.action_confirm_goods) View actionConfirmGoodsView; // 确认接单
        @Bind(R.id.action_confirm_delivery) View actionDeliveryView; // 确认送达
        @Bind(R.id.action_layout) View actionLayoutView;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
