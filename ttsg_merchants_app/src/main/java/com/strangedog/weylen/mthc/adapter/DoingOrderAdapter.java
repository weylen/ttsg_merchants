package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
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
public class DoingOrderAdapter extends ListBaseAdapter<OrderEntity>{

    private LayoutInflater mLayoutInflater;
    private List<OrderEntity> data;

    public DoingOrderAdapter(Context context, List<OrderEntity> data) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_order, parent, false);
        return new A(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public static class A extends RecyclerView.ViewHolder{
        @Bind(R.id.orderStatusView)  TextView mOrderStatusView;
        @Bind(R.id.orderTimeView)  TextView mOrderTimeView;
        @Bind(R.id.orderContentView)  TextView mOrderContentView;
        @Bind(R.id.orderAddrView)  TextView mOrderAddrView;
        @Bind(R.id.orderPriceView)  TextView mOrderPriceView;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
