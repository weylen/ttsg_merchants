package com.strangedog.weylen.mthc.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.rey.material.widget.TextView;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.ProductsActivity;
import com.strangedog.weylen.mthc.SalesActivity;
import com.strangedog.weylen.mthc.activity.addgoods.AddProductsActivity;
import com.strangedog.weylen.mthc.adapter.TabPagerAdapter;
import com.strangedog.weylen.mthc.view.ZViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /******************** 绑定视图 *********************/
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    /******************* 定义属性 *********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.Order));
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        ZViewPager pager = (ZViewPager) findViewById(R.id.viewPager);
        final TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DoingOrderFragment(), "进行中");
        adapter.addFragment(new CompleteOrderFragment(), "已完成");
        pager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        View menuActionView = navigationView.getMenu().findItem(R.id.nav_order).getActionView();
        TextView msg= (TextView) menuActionView.findViewById(R.id.msg);
        if (msg != null){
            msg.setText("9");
        }
        drawerLayout.addDrawerListener(drawerListener);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private boolean isForward;
    Class<?> clazz = null;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_order:
                onBackPressed();
                break;
            case R.id.nav_sales:
                isForward = true;
                clazz = SalesActivity.class;
                break;
            case R.id.nav_products:
                isForward = true;
                clazz = ProductsActivity.class;
                break;
            case R.id.nav_addProducts:
                isForward = true;
                clazz = AddProductsActivity.class;
                break;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.SimpleDrawerListener(){
        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            if (clazz != null && isForward){
                Intent intent = new Intent(IndexActivity.this, clazz);
                startActivity(intent);
            }
            isForward = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (drawerLayout != null){
            drawerLayout.removeDrawerListener(drawerListener);
        }
    }
}
