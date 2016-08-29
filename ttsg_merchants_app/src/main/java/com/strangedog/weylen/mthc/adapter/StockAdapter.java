package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.StockEntity;
import com.strangedog.weylen.mthc.util.DebugUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 */
public class StockAdapter extends ListBaseAdapter<StockEntity>{

    private LayoutInflater mLayoutInflater;
    private Context context;


    public StockAdapter(Context context) {
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_stock, parent, false);
        return new A(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder aHolder, int position) {
        A holder = (A) aHolder;
        StockEntity entity = getItem(position);

        holder.stockEdit.setTag(entity);
        holder.stockEdit.setText(String.valueOf(entity.getNumber()));
        holder.stockEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int number = 0;
                if (!TextUtils.isEmpty(s)){
                    try {
                        number = Integer.parseInt(s.toString());
                    }catch (NumberFormatException e){}
                }else {
                    holder.stockEdit.setText(String.valueOf(0));
                }
                ((StockEntity)holder.stockEdit.getTag()).setNumber(number);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  mDataList == null ? 0 : mDataList.size();
    }

    public static class A extends RecyclerView.ViewHolder{
        @Bind(R.id.imageView) ImageView imageView;
        @Bind(R.id.orderContentView) TextView contentView;
        @Bind(R.id.orderPriceView) TextView priceView;
        @Bind(R.id.text_stock) EditText stockEdit;
        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
