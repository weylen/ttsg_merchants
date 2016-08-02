package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.jdsjlzx.interfaces.OnItemClickLitener;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;

/**
 * Created by weylen on 2016-08-02.
 */
public class ZWrapperAdapter extends LRecyclerViewAdapter{

    public ZWrapperAdapter(Context context, RecyclerView.Adapter innerAdapter) {
        super(context, innerAdapter);
        setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (itemClickListener != null){
                    itemClickListener.onItemClicked(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private ItemClickListener itemClickListener;
    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
