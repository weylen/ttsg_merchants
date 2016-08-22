package com.strangedog.weylen.mthc.activity.productsdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.EditText;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.entity.ProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.CalendarUtil;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-10.
 */
public class ProductsDetailsActivity extends BaseActivity {

    public static final String ENTITY_KEY = "entity_key";

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.image) ImageView mImage;
    @Bind(R.id.itemProductsName) EditText mItemProductsName; // 商品名称 不可修改
    @Bind(R.id.itemPrice) EditText mItemPrice; // 销售价格
    @Bind(R.id.itemInventory) EditText mItemInventory; // 库存 不可修改
    @Bind(R.id.itemPromotion) EditText mItemPromotion; // 促销信息
    @Bind(R.id.itemPromotionPrice) EditText mItemPromotionPrice; // 促销价格
    @Bind(R.id.itemPromotionStart) EditText mItemPromotionStart; // 促销开始时间 只能选择
    @Bind(R.id.itemPromotionEnd) EditText mItemPromotionEnd; // 促销结束时间 只能选择
    @Bind(R.id.itemWrapStart) View itemWrapStart;
    @Bind(R.id.itemWrapEnd) View itemWrapEnd;


    private boolean isEditable; // 是否为编辑模式
    private ProductsEntity productsEntity;

    private int grayText, redText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        grayText = getResources().getColor(R.color.grayText);
        redText = getResources().getColor(R.color.redText);

        // 设置不可编辑的内容
        mItemProductsName.setEnabled(false);
        mItemInventory.setEnabled(false);
        mItemPromotionStart.setKeyListener(null);
        mItemPromotionEnd.setKeyListener(null);

        enableOrDisableView();

        productsEntity = getIntent().getParcelableExtra(ENTITY_KEY);
        if (productsEntity == null){
            showToast("参数出现错误");
            finish();
            return;
        }
        setupInitMessage();
    }

    /**
     * 设置初始化的参数信息
     */
    private void setupInitMessage(){
        // 设置商品名称
        mItemProductsName.setText(productsEntity.getName());
        // 设置商品价格
        mItemPrice.setText(productsEntity.getSalePrice());
        // 设置商品库存
        mItemInventory.setText(productsEntity.getStock());
        // 取得是否有促销信息
        String promotionPrice = productsEntity.getPromote();
        boolean hasPromotion = LocaleUtil.hasPromotion(promotionPrice);
        // 设置促销内容
        if (hasPromotion){ // 有促销
            mItemPromotion.setText(productsEntity.getInfo());
            mItemPromotionPrice.setText(promotionPrice);
            mItemPromotionStart.setText(productsEntity.getBegin());
            mItemPromotionEnd.setText(productsEntity.getEnd());
            mItemPromotionPrice.setTextColor(redText);
        }else {
            mItemPromotion.setText("");
            mItemPromotionPrice.setText("");
            mItemPromotionStart.setText("");
            mItemPromotionEnd.setText("");
            mItemPromotionPrice.setTextColor(grayText);
        }
        // 加载商品图片
        String imgPath = productsEntity.getImgPath();
        Glide.with(this)
                .load(Constants.BASE_URL + imgPath.split(",")[0])
                .fitCenter()
                .placeholder(R.mipmap.img_default)
                .crossFade()
                .dontAnimate()
                .error(R.mipmap.img_default)
                .into(mImage);
    }

    private void enableOrDisableView(){
        if (isEditable){
            mToolbar.setTitle("商品编辑");
            mItemPrice.setEnabled(true);
            mItemPromotionPrice.setEnabled(true);
            mItemPromotion.setEnabled(true);
            mItemPromotionStart.setEnabled(true);
            mItemPromotionEnd.setEnabled(true);
            mItemPromotionStart.setHint("点击设置促销开始时间");
            mItemPromotionEnd.setHint("点击设置促销开始时间");
        }else {
            mToolbar.setTitle("商品详情");
            mItemPrice.setEnabled(false);
            mItemPromotionPrice.setEnabled(false);
            mItemPromotion.setEnabled(false);
            mItemPromotionStart.setEnabled(false);
            mItemPromotionEnd.setEnabled(false);
            mItemPromotionStart.setHint("促销开始时间");
            mItemPromotionEnd.setHint("促销开始时间");
        }
    }

    @OnClick(R.id.itemPromotionStart)
    public void onStartDateClick(){
        if (isEditable){
            showDateDialog(1);
        }
    }

    @OnClick(R.id.itemPromotionEnd)
    public void onEndClick(){
        if (isEditable){
            showDateDialog(2);
        }
    }

    private Calendar mCalendar;
    /**
     * 显示日期对话框
     * @param status 1表示开始时间 2表示结束时间
     */
    private void showDateDialog(int status){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.dateRange(1,0,1970,31,11,2200);
        datePickerDialog.cancelable(false);
        datePickerDialog.date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        datePickerDialog.positiveAction("确定").positiveActionClickListener(v -> {
            datePickerDialog.dismiss();
            mCalendar = datePickerDialog.getCalendar();
            showTimeDialog(status);

        });
        datePickerDialog.negativeAction("取消").negativeActionClickListener(v -> {
            datePickerDialog.dismiss();
            mCalendar = null;
        });
        datePickerDialog.show();
    }

    /**
     * 显示时间对话框
     * @param status 1表示开始时间 2表示结束时间
     */
    private void showTimeDialog(int status){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog pickerDialog = new TimePickerDialog(this);
        pickerDialog.hour(calendar.get(Calendar.HOUR_OF_DAY));
        pickerDialog.cancelable(false);
        pickerDialog.minute(calendar.get(Calendar.MINUTE));
        pickerDialog.positiveAction("确定").positiveActionClickListener(v -> {
            pickerDialog.dismiss();
            if (mCalendar != null){
                mCalendar.set(Calendar.MINUTE, pickerDialog.getMinute());
                mCalendar.set(Calendar.HOUR_OF_DAY, pickerDialog.getHour());
                if (status == 1){
                    mItemPromotionStart.setText(CalendarUtil.getStandardDateTime(mCalendar));
                }else {
                    mItemPromotionEnd.setText(CalendarUtil.getStandardDateTime(mCalendar));
                }
            }
        });

        pickerDialog.negativeAction("取消").negativeActionClickListener(v -> {
            pickerDialog.dismiss();
            mCalendar = null;
        });
        pickerDialog.show();
    }

    private MenuItem menuItemEdit;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products_details, menu);
        menuItemEdit = menu.findItem(R.id.action_edit);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isEditable == false){
                finish();
            }else {
                // 是编辑模式则进行取消操作 然后改变图标
                setupInitMessage(); // 重置内容
                mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);
                menuItemEdit.setIcon(R.mipmap.ic_edit_white);
                isEditable = false;
                enableOrDisableView();
            }

            return true;
        }else if (item.getItemId() == R.id.action_edit){
            if (isEditable){ // 如果之前是可编辑状态
                // 则进行保存操作 然后改变图标
                checkGoodsInfo();
            }else{
                // 不是编辑状态 则进入编辑状态
                mToolbar.setNavigationIcon(R.mipmap.ic_clear_white);
                item.setIcon(R.mipmap.ic_done_white);
                isEditable = !isEditable;
                enableOrDisableView();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void checkGoodsInfo(){
        String salePrice = mItemPrice.getText().toString();
        if (TextUtils.isEmpty(salePrice)){
            showSnakeBar(mItemProductsName, "请输入商品价格");
            return;
        }
        try{
            if (Float.parseFloat(salePrice) <= 0){
                showSnakeBar(mItemProductsName, "商品价格必须大于0");
                return;
            }
        }catch (NumberFormatException e){
            showSnakeBar(mItemProductsName, "商品价格设置错误");
            return;
        }
        String promotionPrice = mItemPromotionPrice.getText().toString();
        String promotionStart = mItemPromotionStart.getText().toString();
        String promotionEnd = mItemPromotionEnd.getText().toString();

        if (!TextUtils.isEmpty(promotionPrice)){
            try{
                float pPrice = Float.parseFloat(promotionPrice);
                if (pPrice <= 0){
                    showSnakeView("促销价格必须大于0");
                    return;
                }
            }catch (NumberFormatException e){
                showSnakeBar(mItemProductsName, "促销价格设置错误");
                return;
            }
            if (TextUtils.isEmpty(promotionStart)){
                showSnakeView("请选择促销开始时间");
                return;
            }
            if (TextUtils.isEmpty(promotionEnd)){
                showSnakeView("请选择促销结束时间");
                return;
            }
            if (CalendarUtil.compare(promotionStart, promotionEnd) >= 0){
                showSnakeView("促销结束时间必须大于促销开始时间");
                return;
            }
            // showAlertDialog
            showAlertDialog();
        }else {
            alertGoodsInfo();
        }
    }

    private void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("确认该商品的促销信息")
                .setMessage("促销价格：￥"+mItemPromotionPrice.getText().toString()
                +"\n促销时间：\n" + mItemPromotionStart.getText().toString() + "~" + mItemPromotionEnd.getText().toString())
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    alertGoodsInfo();
                })
                .show();

    }

    /**
     * 修改商品信息
     */
    private void alertGoodsInfo(){
        showProgressDialog("保存中...");
        String param = map();
        DebugUtil.d("ProductsDetailsActivity 保存参数：" + param);
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .alertGoodsInfo(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        doError();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("ProductsDetailsActivity 修改结果：" + jsonObject);
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);
                            menuItemEdit.setIcon(R.mipmap.ic_edit_white);
                            isEditable = !isEditable;
                            enableOrDisableView();
                            showSnakeView("保存成功");
                        }else {
                            doError();
                        }
                    }
                });
    }

    private String map(){
        String salePrice = mItemPrice.getText().toString();
        String promotionPrice = mItemPromotionPrice.getText().toString();
        String promotionStart = mItemPromotionStart.getText().toString();
        String promotionEnd = mItemPromotionEnd.getText().toString();
        String info = mItemPromotion.getText().toString();
        if (TextUtils.isEmpty(promotionPrice)){
            promotionStart = Constants.EMPTY_STR;
            promotionEnd = Constants.EMPTY_STR;
            info = Constants.EMPTY_STR;
            promotionPrice = String.valueOf(-1);
        }
        JsonArray array = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", productsEntity.getId());
        jsonObject.addProperty("salePrice", salePrice);
        jsonObject.addProperty("promote", promotionPrice);
        jsonObject.addProperty("begin", promotionStart);
        jsonObject.addProperty("end", promotionEnd);
        jsonObject.addProperty("info", info);
        jsonObject.addProperty("added", productsEntity.getStauts());
        array.add(jsonObject);
        return array.toString();
    }

    private void doError(){
        dismissProgressDialog();
        showSnakeView("保存失败，请重新操作");
    }

    private void showSnakeView(String message){
        showSnakeBar(mItemProductsName, message);
    }
}
