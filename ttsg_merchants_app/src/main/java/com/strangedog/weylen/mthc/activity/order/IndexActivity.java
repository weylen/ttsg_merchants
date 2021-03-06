package com.strangedog.weylen.mthc.activity.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cd.weylen.appupdatelibrary.AppUpdate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.BaseApplication;
import com.strangedog.weylen.mthc.ProductsActivity;
import com.strangedog.weylen.mthc.activity.addgoods.AddProductsActivity;
import com.strangedog.weylen.mthc.activity.login.LoginActivity;
import com.strangedog.weylen.mthc.activity.login.LoginData;
import com.strangedog.weylen.mthc.activity.promotion_goods.PromotionGoodsActivity;
import com.strangedog.weylen.mthc.activity.sales.SalesActivity;
import com.strangedog.weylen.mthc.activity.stock.StockActivity;
import com.strangedog.weylen.mthc.activity.withdrawal.WithdrawalActivity;
import com.strangedog.weylen.mthc.adapter.TabPagerAdapter;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.RespSubscribe;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.prefs.NewVersionData;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DeviceUtil;
import com.strangedog.weylen.mthc.util.DialogUtil;
import com.strangedog.weylen.mthc.util.LocaleUtil;
import com.strangedog.weylen.mthc.util.MediaUtil;
import com.strangedog.weylen.mthc.view.TimeDialog;
import com.strangedog.weylen.mthc.view.ZViewPager;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

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
    private TextView balanceView, statusView, timeView, nightTimeView;
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

//        View menuActionView = navigationView.getMenu().findItem(R.id.nav_order).getActionView();
//        if (menuActionView != null){
//            TextView msg= (TextView) menuActionView.findViewById(R.id.msg);
//            if (msg != null){
//                msg.setText("9");
//                msg.setVisibility(View.GONE);
//            }
//        }

//        drawerLayout.addDrawerListener(drawerListener);

        View headerView = navigationView.getHeaderView(0);
        // 商家状态
        statusView = (TextView) headerView.findViewById(R.id.status);
        //
        statusView.setOnClickListener(v -> showStatusPopup());
        // 营业时间
        timeView = (TextView) headerView.findViewById(R.id.time);
        timeView.setOnClickListener(v -> showTimeDialog(false));
        // 直营时间
        nightTimeView = (TextView) headerView.findViewById(R.id.nightTime);
        nightTimeView.setOnClickListener(v->showNightTimeDialog(false));
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

        // 注册别名
        if (!TextUtils.isEmpty(MiPushClient.getRegId(this))){
            MiPushClient.setAlias(this, DeviceUtil.INSTANCE.getDeviceUuid(this), null);
        }else {
            MiPushClient.registerPush(this, BaseApplication.APP_ID, BaseApplication.APP_KEY);
        }

        // 获取商家状态
        requestStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isPlay;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_volume_off){
//            if (isPlay){
//                MediaUtil.stop();
//            }else {
//                MediaUtil.paly(this);
//            }
//            isPlay =!isPlay;
            MediaUtil.stop();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimeDialog(boolean isMust){
        ShopData shopData = ShopData.INSTANCE;
        TimeDialog timeDialog = new TimeDialog(this, shopData.startTime, shopData.endTime);
        timeDialog.setOnDataListener((startTime, endTime) -> setTradeTime(TimeDialog.TYPE_DAY, startTime, endTime));
        timeDialog.setOnCancelListener(dialog -> {
            if (isMust){
                finish();
            }
        });
        timeDialog.show(TimeDialog.TYPE_DAY);
    }

    private void showNightTimeDialog(boolean isMust){
        ShopData shopData = ShopData.INSTANCE;
        TimeDialog timeDialog = new TimeDialog(this, shopData.nightStart, shopData.nightEnd);
        timeDialog.setOnDataListener((startTime, endTime) -> setTradeTime(TimeDialog.TYPE_NIGHT, startTime, endTime));
        timeDialog.setOnCancelListener(dialog -> {
            if (isMust){
                finish();
            }
        });
        timeDialog.show(TimeDialog.TYPE_NIGHT);
    }

    private void showStatusPopup(){
        PopupMenu p  = new PopupMenu(this, statusView);
        p.getMenu().add(0, 1, 0, "正在营业");
        p.getMenu().add(0, 2, 0, "停业中");
        p.getMenu().add(0, 3, 0, "休息中");
        p.setOnMenuItemClickListener(item -> {
            showStateDialog(item.getItemId(), item.getTitle().toString());
            return false;
        });
        p.show();
    }

    private void showStateDialog(int state, String stateStr){
        new  AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定设置营业状态为：" + stateStr)
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    setTradeStatus(state);
                })
                .show();
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
            case R.id.nav_promotion:
                isForward = true;
                clazz = PromotionGoodsActivity.class;
                break;
            case R.id.nav_settings:
                showDeliveryDialog();
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

    /**
     * 显示配送费对话框
     */
    private void showDeliveryDialog(){
        View layout = getLayoutInflater().inflate(R.layout.delivery_layout, null, false);
        final EditText editText1 = (EditText) layout.findViewById(R.id.item_text1);
        final EditText editText2 = (EditText) layout.findViewById(R.id.item_text2);
        editText1.setText(ShopData.INSTANCE.fareLimit);
        editText2.setText(ShopData.INSTANCE.fare);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("配送费设置")
                .setView(layout)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setShow(dialog, true);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text1 = editText1.getText().toString();
                        String text2 = editText2.getText().toString();
                        setShow(dialog, false);
                        if (TextUtils.isEmpty(text1) || TextUtils.isEmpty(text2)){
                            showToast("请输入完整的数据");
                            return;
                        }
                        if (LocaleUtil.isLessThanZero(text1) || LocaleUtil.isLessThanZero(text2)){
                            showToast("输入的数字不能小于0");
                            return;
                        }
                        setShow(dialog, true);
                        dialog.dismiss();

                        remoteDelivery(text2, text1);
                    }
                });
        builder.create().show();
    }

    private void setShow(DialogInterface dialog, boolean mShowing) {
        try {
            Field field = dialog.getClass().getSuperclass().getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, mShowing);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
                            clearAlias();
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
                            balanceView.setText("￥ " + LocaleUtil.formatMoney(balance));
                        }
                    }
                });
    }

    private void clearAlias(){
        MiPushClient.unsetAlias(this, DeviceUtil.INSTANCE.getDeviceUuid(this), null);
    }

    private void doNewVersion(){
        NewVersionData data = NewVersionData.INSTANCE;
        if (data.isNewVersion){
            AppUpdate update = new AppUpdate.Builder(this).message(data.desc)
                    .isMust(data.isMust)
                    .downloadUrl(data.downloadUrl)
                    .callback(new AppUpdate.OnUpdateCallbackListener() {
                        @Override
                        public void onCancel(boolean b) {
                            if (b){
                                finish();
                            }
                        }

                        @Override
                        public void disableMemory(boolean b) {
                            if (b){
                                finish();
                            }
                        }

                        @Override
                        public void downloadFailure(boolean isMust) {

                        }
                    }).create();
            String savePath = DeviceUtil.getExternalCacheDir(this);
            update.setSaveFile(savePath);
            update.show();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        new Handler().postDelayed(() -> doNewVersion(), 300);
    }

    private void requestStatus(){
        String areaId = LoginData.INSTANCE.getAccountEntity(this).getArea();
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getTradeState(areaId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(jsonObject -> {
                    DebugUtil.d("IndexActivity 获取商家状态：" + jsonObject);
                    if (ResponseMgr.getStatus(jsonObject) == 1){
                        JsonObject data = jsonObject.get("data").getAsJsonObject().get(areaId).getAsJsonObject();
                        String stateStr = data.get("tradeState").getAsString();
                        if (!TextUtils.isEmpty(stateStr)){
                            int state = formatState(stateStr);
                            if (state != -1){
                                ShopData.INSTANCE.status = state;
                                statusView.setText(ShopData.INSTANCE.getStatus());
                            }
                        }
                        boolean isSetTime = false;
                        String timeStr = data.get("tradeTime").getAsString();
                        if (!TextUtils.isEmpty(timeStr)){
                            Gson gson = new Gson();
                            JsonObject timeObject = gson.fromJson(timeStr, JsonObject.class);
                            ShopData.INSTANCE.startTime = timeObject.get("start").getAsString();
                            ShopData.INSTANCE.endTime = timeObject.get("end").getAsString();
                            timeView.setText(getShowTime(ShopData.INSTANCE.startTime, ShopData.INSTANCE.endTime));
                            isSetTime = true;
                        }

                        boolean isSetNightTime = false;
                        String nightStr = data.get("night").getAsString();
                        if (!TextUtils.isEmpty(nightStr)){
                            Gson gson = new Gson();
                            JsonObject timeObject = gson.fromJson(nightStr, JsonObject.class);
                            ShopData.INSTANCE.nightStart = timeObject.get("start").getAsString();
                            ShopData.INSTANCE.nightEnd = timeObject.get("end").getAsString();
                            nightTimeView.setText(getShowTime(ShopData.INSTANCE.nightStart, ShopData.INSTANCE.nightEnd));
                            isSetNightTime = true;
                        }

                        // 配送费数据
                        ShopData.INSTANCE.fare = data.get("fare").getAsString();
                        ShopData.INSTANCE.fareLimit = data.get("fareLimit").getAsString();

                        if (isSetTime == false){
                            showSetTimeDialog();
                        }
                        if (isSetNightTime == false){
                            showSetNightTimeDialog();
                        }
                    }
                });
    }

    private void showSetTimeDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你必须设置营业时间才能营业，是否立即设置？")
                .setNegativeButton("取消", (dialog1, which) -> {
                    dialog1.dismiss();
                    finish();
                })
                .setPositiveButton("确定", (dialog1, which) -> {
                    dialog1.dismiss();
                    showTimeDialog(true);
                }).create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showSetNightTimeDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你必须设置直营时间才能营业，是否立即设置？")
                .setNegativeButton("取消", (dialog1, which) -> {
                    dialog1.dismiss();
                    finish();
                })
                .setPositiveButton("确定", (dialog1, which) -> {
                    dialog1.dismiss();
                    showNightTimeDialog(true);
                }).create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private int formatState(String state){
        try{
            return Integer.parseInt(state);
        }catch (Exception e){

        }
        return -1;
    }

    private void setTradeTime(int type, String startTime, String endTime){
        showProgressDialog("修改中...");
        JSONObject param = new JSONObject();
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("start", startTime);
            jsonObject.put("end", endTime);
            JSONObject oldTime = new JSONObject();

            if (type == TimeDialog.TYPE_NIGHT){ // 设置直营时间 那么day不变
                oldTime.put("start", ShopData.INSTANCE.startTime);
                oldTime.put("end", ShopData.INSTANCE.endTime);
                param.put("day", oldTime);
                param.put("night", jsonObject);
            }else if (type == TimeDialog.TYPE_DAY){
                oldTime.put("start", ShopData.INSTANCE.nightStart);
                oldTime.put("end", ShopData.INSTANCE.nightEnd);
                param.put("day", jsonObject);
                param.put("night", oldTime);
            }
        }catch (JSONException e){
            DebugUtil.d("IndexActivity setTradeTime 出现异常");
            return;
        }

        DebugUtil.d("IndexActivity 设置时间：" + param.toString());
        HttpService hs = RetrofitFactory.getRetrofit().create(HttpService.class);
        hs.setTradeTimeState(param.toString());


        RetrofitFactory.getRetrofit().create(HttpService.class)
                .setTradeTimeState(param.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        showToast("设置失败，请重试");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            if (type == TimeDialog.TYPE_DAY){
                                ShopData.INSTANCE.startTime = startTime;
                                ShopData.INSTANCE.endTime = endTime;
                                timeView.setText(getShowTime(startTime, endTime));
                            }else if(type == TimeDialog.TYPE_NIGHT){
                                ShopData.INSTANCE.nightStart = startTime;
                                ShopData.INSTANCE.nightEnd = endTime;
                                nightTimeView.setText(getShowTime(startTime, endTime));
                            }

                            showToast("设置成功");
                        }else {
                            showToast("设置失败，请重试");
                        }
                    }
                }));
    }

    /**
     * 设置商家状态
     * @param status 1是正常营业，2是停业，3是休业
     * @return
     */
    private void setTradeStatus(int status){
        showProgressDialog("修改中...");
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .setTradeState(status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        showToast("设置失败，请重试");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            ShopData.INSTANCE.status = status;
                            statusView.setText(ShopData.INSTANCE.getStatus());
                            showToast("设置成功");
                        }else {
                            showToast("设置失败，请重试");
                        }
                    }
                }));
    }

    private static String getShowTime(String startTime, String endTime){
        return startTime +" ~ " + endTime;
    }

    private void remoteDelivery(String delivery, String text){
        showProgressDialog("设置中...");
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .deliverySetting(delivery, text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        showToast("设置失败，请重试");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            showToast("设置成功");
                            ShopData.INSTANCE.fare = delivery;
                            ShopData.INSTANCE.fareLimit = text;
                        }else {
                            showToast("设置失败，请重试");
                        }
                    }
                }));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); //调用双击退出函数
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            BaseApplication.exit();
            System.exit(0);
        }
    }
}
