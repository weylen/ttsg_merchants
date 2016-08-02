package com.strangedog.weylen.mthc.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.KindDataEntity;
import com.strangedog.weylen.mthc.iinter.ItemClickListener;
import com.strangedog.weylen.mthc.util.DimensUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by weylen on 2016-08-01.
 */
public class ListBottomSheetDialog extends BottomSheetDialog {

    @Bind(R.id.recyclerView) ZRecyclerView mRecyclerView;

    private MyArrayAdapter arrayAdapter;
    private List<KindDataEntity> itemDatas;
    private View contentView;
    private ItemClickListener itemClickListener;

    public ListBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemData(List<KindDataEntity> itemDatas){
        this.itemDatas = itemDatas;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = getLayoutInflater().inflate(R.layout.layout_list_bottomsheet, null);
        setContentView(contentView);
        ButterKnife.bind(this, contentView);

        // 设置布局容器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // 添加分割线
        int divider = getContext().getResources().getColor(R.color.divider);
        mRecyclerView.addItemDecoration(new ItemDividerDecoration().setSize(DimensUtil.dp2px(getContext(), 0.5f)).setColor(divider)
                .setShowFooterDivider(false));
        mRecyclerView.setHasFixedSize(true);

        arrayAdapter = new MyArrayAdapter(position -> {
            if (itemClickListener != null){itemClickListener.onItemClicked(position);}
        });
        mRecyclerView.setAdapter(arrayAdapter);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }

    class MyArrayAdapter extends RecyclerView.Adapter<A>{

        private ItemClickListener itemClickListener;
        MyArrayAdapter(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public A onCreateViewHolder(ViewGroup parent, int viewType) {
            A holder = new A(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false),
                    itemClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(A holder, int position) {
            KindDataEntity entity = itemDatas.get(position);
            holder.textView.setText(entity.getName());
        }

        @Override
        public int getItemCount() {
            return itemDatas == null ? 0 : itemDatas.size();
        }
    }

    static class A extends RecyclerView.ViewHolder{

        @Bind(android.R.id.text1) TextView textView;

        public A(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v->{if (itemClickListener != null){
                itemClickListener.onItemClicked(getLayoutPosition());
            }});
        }
    }
}
