package com.strangedog.weylen.mthc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.OrderEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 */
public class OrderAdapter extends WrapperAdapterData<OrderEntity, OrderAdapter.A>{

    public OrderAdapter(LayoutInflater inflater, List<OrderEntity> data) {
        super(inflater, data);
    }

    @Override
    public A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_order, parent, false);
        return new A(view);
    }

    @Override
    public void onBindViewHolder(A holder, int position) {
        OrderEntity entity = getItem(position);
        //TODO 设置数据
    }


    public static class A extends RecyclerView.ViewHolder{

        @Bind(R.id.orderStatusView)  TextView mOrderStatusView;
        @Bind(R.id.orderTimeView)  TextView mOrderTimeView;
        @Bind(R.id.orderContentView)  TextView mOrderContentView;
        @Bind(R.id.orderPriceView)  TextView mOrderPriceView;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
