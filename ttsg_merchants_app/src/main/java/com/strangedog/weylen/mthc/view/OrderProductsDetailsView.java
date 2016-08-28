package com.strangedog.weylen.mthc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.OrderDetailsProductsEntity;
import com.strangedog.weylen.mthc.entity.OrderProductsEntity;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import java.util.List;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderProductsDetailsView extends LinearLayout{

    public OrderProductsDetailsView(Context context) {
        super(context);
        init();
    }

    public OrderProductsDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrderProductsDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setDataAndNotify1(List<OrderProductsEntity> data){
        removeAllViews();
        if (LocaleUtil.isListEmpty(data)){
            return;
        }
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        for (OrderProductsEntity entity : data){
            View view = mInflater.inflate(R.layout.layout_orderproducts_details, this, false);
            // 设置信息
            TextView nameView = (TextView) view.findViewById(R.id.text_name);
            nameView.setText(entity.getName() + "  x" + entity.getAmount());
            TextView priceView = (TextView) view.findViewById(R.id.text_price);
            priceView.setText("￥" + entity.getTotal());
            addView(view);
        }
    }

    public int setDataAndNotify2(List<OrderDetailsProductsEntity> data){
        removeAllViews();
        if (LocaleUtil.isListEmpty(data)){
            return 0;
        }
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        int totalCount = 0;
        for (OrderDetailsProductsEntity entity : data){
            View view = mInflater.inflate(R.layout.layout_orderproducts_details, this, false);
            // 设置信息
            TextView nameView = (TextView) view.findViewById(R.id.text_name);
            nameView.setText(entity.getName() + "  x" + entity.getAmount());
            TextView priceView = (TextView) view.findViewById(R.id.text_price);
            priceView.setText("￥" + entity.getTotal());
            addView(view);

            totalCount += formartAmount(entity.getAmount());
        }
        return totalCount;
    }

    private int formartAmount(String amount){
        int count = 0;
        try {
            count = Integer.parseInt(amount);
        }catch (NumberFormatException e){}
        return count;
    }
}
