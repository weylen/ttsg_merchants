package com.strangedog.weylen.mthc.activity.orderdetails;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.entity.OrderDetailsEntity;
import com.strangedog.weylen.mthc.entity.OrderDetailsProductsEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DimensUtil;
import com.strangedog.weylen.mthc.view.OrderProductsDetailsView;
import com.strangedog.weylen.mthc.view.ZRefreshView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-24.
 */
public class OrderDetailsActivity extends BaseActivity implements OrderDetailsView{

    public static final String ORDER_KEY = "order_key";

    @Bind(R.id.orderCodeView) TextView mOrderCodeView;
    @Bind(R.id.orderStatusView) TextView mOrderStatusView;
    @Bind(R.id.orderTimeView) TextView mOrderTimeView;
    @Bind(R.id.orderDeliveryView) TextView mOrderDeliveryView;
    @Bind(R.id.orderNoteView) TextView mOrderNoteView;
    @Bind(R.id.orderProductsPriceView) TextView mOrderProductsPriceView;
    @Bind(R.id.orderPaymentView) TextView mOrderPaymentView;
    @Bind(R.id.orderContactsView) TextView mOrderContactsView;
    @Bind(R.id.orderAddressView) TextView mOrderAddressView;
    @Bind(R.id.layout_products) OrderProductsDetailsView orderProductsDetailsView;
    @Bind(R.id.layout_parent) FrameLayout parentLayout;
    @Bind(R.id.layout_main) View mainView;
    @Bind(R.id.layout_empty) View emptyView;
    @Bind(R.id.refreshLayout) ZRefreshView zRefreshingView;
    @Bind(R.id.layout_note) View noteLayoutView;
    // 确认收货
    @Bind(R.id.confirm_layout) View confirmViewLayout;
    @Bind(R.id.confirm_text) TextView confrimTextView;

    private String orderNumber = null; // 订单号
    private List<OrderDetailsEntity> detailsEntities;
    private OrderDetailsPresenter orderDetailsPresenter;

    private boolean isFirstRequest = true;
    private boolean isAutoRefresh;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyorder);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderNumber = getIntent().getStringExtra(ORDER_KEY);
        if (TextUtils.isEmpty(orderNumber)) {
            return;
        }
        zRefreshingView.setOnRefreshListener(() ->{
            if (!isAutoRefresh){
                orderDetailsPresenter.onRefresh();
            }
            isAutoRefresh = false;
        });

        emptyView.setOnClickListener(v -> orderDetailsPresenter.onStartRequestOrderDetails(orderNumber));

        // 隐藏所有的布局
        mainView.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.GONE);
        orderDetailsPresenter = new OrderDetailsPresenter(this);
    }

    private void request(){
        isFirstRequest = true;
        ViewTreeObserver vto2 = mOrderNoteView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mOrderNoteView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (isFirstRequest){
                    orderDetailsPresenter.onStartRequestOrderDetails(orderNumber);
                    isFirstRequest = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        request();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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

    @Override
    public void onBackPressed() {
        finish();
    }

    // 确认接单，送达点击事件
    @OnClick(R.id.confirm_text)
    void onCofirClick(){
        String status = detailsEntities.get(0).getProducts().get(0).getStauts();
        String orderId = detailsEntities.get(0).getOrderId();
        if ("3".equalsIgnoreCase(status)){
            showDialog("确认接单？", orderId, 7);
        }else if ("7".equalsIgnoreCase(status)) {
            showDialog("确认送达？", orderId, 8);
        }
    }

    private void showDialog(String message, String orderId, int status){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    orderDetailsPresenter.alertOrderStatus(orderId, status);
                })
                .show();
    }

    /**
     * 设置订单信息
     */
    private void setupMessages(){
        OrderDetailsEntity detailsEntity = detailsEntities.get(0);
        // 订单号
        mOrderCodeView.setText(detailsEntity.getOrderId());
        // 订单总金额
        mOrderPaymentView.setText(detailsEntity.getTotal());
        // 获取商品列表信息
        List<OrderDetailsProductsEntity> products = detailsEntity.getProducts();
        OrderDetailsProductsEntity productsEntity = products.get(0);
        // 订单状态
        mOrderStatusView.setText(Constants.ORDER_PARAM.get(productsEntity.getStauts()));
        // 下单时间
        mOrderTimeView.setText(productsEntity.getDate());
        // 送达时间
        mOrderDeliveryView.setText(productsEntity.getShsj());
        // 备注
        String text = productsEntity.getNote();
        String note = text == null ? "" : text;
        DebugUtil.d("OrderDetailsActivity 备注：" + note);
        float textWidth = mOrderNoteView.getPaint().measureText(note);
        Drawable d = getResources().getDrawable(R.mipmap.abc_arrow_right);
        if (textWidth >= mOrderNoteView.getWidth()){
            mOrderNoteView.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
            mOrderNoteView.setCompoundDrawablePadding(DimensUtil.dp2px(this, 10));
            noteLayoutView.setOnClickListener(v-> showNoteDialog(note));
        }else {
            mOrderNoteView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        mOrderNoteView.setText(note);
        // 联系人
        mOrderContactsView.setText(productsEntity.getFname() + "   " + productsEntity.getTele());
        // 地址
        mOrderAddressView.setText(productsEntity.getAddr());
        // 商品价格
        mOrderProductsPriceView.setText("￥" + detailsEntity.getTotal());
        orderProductsDetailsView.setDataAndNotify2(products);
        // 设置支付状态
        String status = productsEntity.getStauts();

        // 3：已支付未发货 6：支付确认中 7：商家已结单 8：商家已送达
        if ("1".equalsIgnoreCase(status) || "8".equalsIgnoreCase(status)){ //
            confirmViewLayout.setVisibility(View.GONE);
        }else if ("3".equalsIgnoreCase(status)){
            confirmViewLayout.setVisibility(View.VISIBLE);
            confrimTextView.setText("确认接单");
        }else if ("7".equalsIgnoreCase(status)){
            confirmViewLayout.setVisibility(View.VISIBLE);
            confrimTextView.setText("确认送达");
        }
    }

    private void showNoteDialog(String note){
        new AlertDialog.Builder(this)
                .setTitle("备注信息")
                .setMessage(note)
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void showEmptyView(){
        emptyView.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
    }

    @Override
    public void onStartRequestOrderDetails() {
        isAutoRefresh = true;
        zRefreshingView.setRefreshing(true);
    }

    @Override
    public void onRequestOrderDetailsSuccess(List<OrderDetailsEntity> data) {
        zRefreshingView.setRefreshing(false);
        this.detailsEntities = data;
        isAutoRefresh = false;
        if (data == null){
            showEmptyView();
        }else {
            emptyView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
        setupMessages();
    }

    @Override
    public void onRequestOrderDetailsFailure() {
        zRefreshingView.setRefreshing(false);
        isAutoRefresh = false;
        showEmptyView();
    }

    @Override
    public void onStartAlertStatus() {
        showProgressDialog("处理中...");
    }

    @Override
    public void onAlertStatusFailure(int status) {
        dismissProgressDialog();
        showSnakeBar(confirmViewLayout, "修改订单状态失败，请重试");
    }

    @Override
    public void onAlertStatusSuccess(int status) {
        dismissProgressDialog();
        detailsEntities.get(0).getProducts().get(0).setStauts(String.valueOf(status));
        setupMessages();
    }
}
