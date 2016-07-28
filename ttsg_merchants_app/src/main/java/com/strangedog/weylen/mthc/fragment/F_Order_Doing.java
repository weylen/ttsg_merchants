package com.strangedog.weylen.mthc.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.adapter.OrderAdapter;
import com.strangedog.weylen.mthc.entity.OrderEntity;
import com.strangedog.weylen.mthc.util.AppPrams;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.view.DefaultHeader;
import com.strangedog.weylen.mthc.view.SpaceItemDecoration;
import com.strangedog.weylen.mthc.view.SpringView;
import com.strangedog.weylen.mthc.view.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-07-02.
 */
public class F_Order_Doing extends BaseFragment {

    ZRecyclerView mRecyclerView;
    SpringView springView;

    private OrderAdapter adapter;

    @Override
    public int layoutId() {
        return R.layout.layout_generic_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        ButterKnife.bind(this, view);
        mRecyclerView = (ZRecyclerView) view.findViewById(R.id.recyclerView);
        springView = (SpringView) view.findViewById(R.id.springView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(DimensUtil.dp2px(getActivity(), AppPrams.DIVIDER), 1));

        adapter = new OrderAdapter(LayoutInflater.from(getActivity()), initData());
        mRecyclerView.setAdapter(adapter);

        final Handler handler = new Handler();
//        springView.setHeader(new DefaultHeader(getActivity()));
        springView.setHeader(new DefaultHeader());
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(()->{
                    springView.onFinishFreshAndLoad();
                }, 2000);
            }
            @Override
            public void onLoadmore() {
            }
        });
    }

    private List<OrderEntity> initData(){
        List<OrderEntity> data = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            data.add(new OrderEntity());
        }
        return data;
    }

}
