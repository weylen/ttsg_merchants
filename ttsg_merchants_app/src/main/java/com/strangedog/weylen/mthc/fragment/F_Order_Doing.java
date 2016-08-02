package com.strangedog.weylen.mthc.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseFragment;
import com.strangedog.weylen.mthc.adapter.OrderAdapter;
import com.strangedog.weylen.mthc.view.SpringView;
import com.strangedog.weylen.mthc.view.ZRecyclerView;

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

    }

}
