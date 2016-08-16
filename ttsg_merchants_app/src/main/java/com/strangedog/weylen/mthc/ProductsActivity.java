package com.strangedog.weylen.mthc;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.activity.shelvesgoos.InSellingGoodsFragment;
import com.strangedog.weylen.mthc.activity.shelvesgoos.ShelvesGoodsFragment;
import com.strangedog.weylen.mthc.adapter.TabPagerAdapter;
import com.strangedog.weylen.mthc.view.ZViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductsActivity extends BaseActivity {

    /******************** 绑定视图 *********************/
    @Bind(R.id.viewPager) ZViewPager mViewPager;
    /******************* 定义属性 *********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InSellingGoodsFragment(), "在售");
        adapter.addFragment(new ShelvesGoodsFragment(), "下架");
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
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
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
