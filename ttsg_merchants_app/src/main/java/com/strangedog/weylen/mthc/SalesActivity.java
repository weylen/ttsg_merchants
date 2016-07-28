package com.strangedog.weylen.mthc;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.adapter.SalesAdapter;
import com.strangedog.weylen.mthc.entity.SalesEntity;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.view.ItemDividerDecoration;
import com.strangedog.weylen.mthc.view.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SalesActivity extends BaseActivity {

    @Bind(R.id.salesPriceView) TextView mSalesPriceView;
    @Bind(R.id.recyclerView)
    ZRecyclerView mRecyclerView;

    private SalesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        int divider = getResources().getColor(R.color.divider);
        mRecyclerView.addItemDecoration(new ItemDividerDecoration().setSize(DimensUtil.dp2px(this, 1)).setColor(divider));

        adapter = new SalesAdapter(LayoutInflater.from(this), initData());
        mRecyclerView.setAdapter(adapter);
    }

    private List<SalesEntity> initData(){
        List<SalesEntity> data = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            data.add(new SalesEntity());
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
