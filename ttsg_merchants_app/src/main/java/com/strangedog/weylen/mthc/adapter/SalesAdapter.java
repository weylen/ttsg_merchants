package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.OrderDetailsEntity;
import com.strangedog.weylen.mthc.iinter.ItemViewClickListenerWrapper;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 */
public class SalesAdapter extends ListBaseAdapter<OrderDetailsEntity>{

    private LayoutInflater mLayoutInflater;

    public SalesAdapter(Context context) {
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_order, parent, false);
        return new A(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder aHolder, int position) {
        A holder = (A) aHolder;
    }

    @Override
    public int getItemCount() {
        return  mDataList == null ? 0 : mDataList.size();
    }

    public static class A extends RecyclerView.ViewHolder{
        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
