package com.strangedog.weylen.mthc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.SalesEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-03.
 */
public class SalesAdapter extends WrapperAdapterData<SalesEntity, SalesAdapter.A> {

    public SalesAdapter(LayoutInflater layoutInflater, List<SalesEntity> data) {
        super(layoutInflater, data);
    }

    @Override
    public A onCreateViewHolder(ViewGroup parent, int viewType) {
        return new A(getLayoutInflater().inflate(R.layout.item_sales, parent, false));
    }

    @Override
    public void onBindViewHolder(A holder, int position) {
        SalesEntity entity = getItem(position);
        //TODO 设置数据
    }

    public static class A extends RecyclerView.ViewHolder {

        @Bind(R.id.orderTimeView) TextView mOrderTimeView;
        @Bind(R.id.orderContentView) TextView mOrderContentView;
        @Bind(R.id.orderPriceView) TextView mOrderPriceView;

        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
