package com.strangedog.weylen.mthc.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.ProductsActivity;
import com.strangedog.weylen.mthc.activity.addgoods.AddProductsActivity;
import com.strangedog.weylen.mthc.activity.login.LoginActivity;
import com.strangedog.weylen.mthc.activity.login.LoginData;
import com.strangedog.weylen.mthc.activity.sales.SalesActivity;
import com.strangedog.weylen.mthc.activity.stock.StockActivity;
import com.strangedog.weylen.mthc.activity.withdrawal.WithdrawalActivity;
import com.strangedog.weylen.mthc.adapter.TabPagerAdapter;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DialogUtil;
import com.strangedog.weylen.mthc.view.ZViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IndexActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /******************** 绑定视图 *********************/
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    /******************* 定义属性 *********************/
    private TextView balanceView;
    private ImageView refreshImgView;
    private Animation animation;
    private String balance; // 余额

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
//        drawerLayout.addDrawerListener(drawerListener);

        View headerView = navigationView.getHeaderView(0);
        // 店铺名字
        TextView shopView = (TextView) headerView.findViewById(R.id.text_shop);
        shopView.setText(LoginData.INSTANCE.getAccountEntity(this).getShoper());
        // 余额
        balanceView = (TextView) headerView.findViewById(R.id.text_balance);
        // 提现
        headerView.findViewById(R.id.btn_withdrawal)
                .setOnClickListener(v -> {
                    Intent intent = new Intent(IndexActivity.this, WithdrawalActivity.class);
                    intent.putExtra("Balance", balance);
                    startActivity(intent);
                });

        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(300);

        // 刷新
        refreshImgView = (ImageView) headerView.findViewById(R.id.img_refresh);
        refreshImgView.setOnClickListener(v -> {
            refreshImgView.startAnimation(animation);
            balance();
        });
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
            case R.id.nav_logout:
                DialogUtil.showAlertDialog(this, "确定要注销？", (dialog, which) -> {
                    dialog.dismiss();
                    logout();
                });
                break;
            case R.id.nav_setStock:
                isForward = true;
                clazz = StockActivity.class;
                break;
        }

        if (clazz != null && isForward){
            Intent intent = new Intent(IndexActivity.this, clazz);
            startActivity(intent);
        }
        isForward = false;

//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }
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
    protected void onResume() {
        super.onResume();
        balance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (drawerLayout != null){
            drawerLayout.removeDrawerListener(drawerListener);
        }
    }

    // 注销
    private void logout(){
        showProgressDialog("注销中...");
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        showSnakeBar(mToolbar, "注销失败，请重新操作");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            showToast("注销成功");
                            LoginData.INSTANCE.logout(IndexActivity.this);
                            Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else {
                            showSnakeBar(mToolbar, "注销失败，请重新操作");
                        }
                    }
                });
    }

    private void balance(){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getBalance()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        animation.cancel();
                        DebugUtil.d("IndexActivity 获取余额失败：" + e.getMessage());
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        animation.cancel();
                        DebugUtil.d("IndexActivity 获取余额成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            balance = jsonObject.get("data").getAsString();
                            balanceView.setText("账户余额：" + balance);
                        }
                    }
                });
    }
}
