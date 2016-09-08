package com.strangedog.weylen.mthc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.entity.StockEntity;
import com.strangedog.weylen.mthc.entity.WithdrawRecordEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-07-02.
 */
public class WithdrawRecordAdapter extends ListBaseAdapter<WithdrawRecordEntity>{

//    1:为商家申请提现
//    2:为提现金额不足
//    3:为进货抵扣货款
//    4:为支付宝接口异常
//    5:为微信接口异常
//    6:为银行接口异常
//    9:为提现成功

    private static HashMap<String, String> status = null;
    static{
        status = new HashMap<>();
        status.put("1", "处理中...");
        status.put("2", "提现金额不足");
        status.put("3", "进货抵扣货款");
        status.put("4", "支付宝接口异常");
        status.put("5", "微信接口异常");
        status.put("6", "银行接口异常");
        status.put("9", "提现成功");
    }

    private LayoutInflater mLayoutInflater;
    private Context context;

    public WithdrawRecordAdapter(Context context) {
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public A onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_withdraw_record, parent, false);
        return new A(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder aHolder, int position) {
        A holder = (A) aHolder;
        WithdrawRecordEntity entity = getItem(position);
        holder.applyPriceView.setText("￥" + LocaleUtil.formatMoney(entity.getQuota()));
        holder.applyTimeView.setText(entity.getApply());
        holder.doStatusView.setText(status.get(entity.getStatus()));
        holder.doTimeView.setText(entity.getDeal());
    }

    @Override
    public int getItemCount() {
        return  mDataList == null ? 0 : mDataList.size();
    }

    public static class A extends RecyclerView.ViewHolder{
        @Bind(R.id.item_text1) TextView applyPriceView;
        @Bind(R.id.item_text2) TextView applyTimeView;
        @Bind(R.id.item_text3) TextView doStatusView;
        @Bind(R.id.item_text4) TextView doTimeView;
        public A(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
