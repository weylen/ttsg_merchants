package com.strangedog.weylen.mthc.activity.withdrawal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.activity.withdraw_record.WithDrawRecordActivity;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.LocaleUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-29.
 */
public class WithdrawalActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.inputBalance) EditText balanceEdit; // 余额
    @Bind(R.id.confirm_withdrawal) TextView confirmWithdrawalView; // 确认转出
    @Bind(R.id.text_enable_balance) TextView enableBalanceView;

    private String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        balance = getIntent().getStringExtra("Balance");
        // 获取余额
        balance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_withdraw, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else if (item.getItemId() == R.id.action_record){
            startActivity(new Intent(this, WithDrawRecordActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged(value = R.id.inputBalance, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChanged(CharSequence text){
        if (TextUtils.isEmpty(text)){
            confirmWithdrawalView.setEnabled(false);
        }else {
            confirmWithdrawalView.setEnabled(true);
        }
    }

    // 点击全部提现
    @OnClick(R.id.text_all_balance)
    void onAllBalanceClick(){
        balanceEdit.setText(LocaleUtil.formatMoney(balance));
    }

    /**
     * 点击确认转出
     */
    @OnClick(R.id.confirm_withdrawal)
    void onConfirmDrawablClick(){
        String input = balanceEdit.getText().toString();
        double inputBalance = Double.parseDouble(input);
        double total = TextUtils.isEmpty(balance) ? 0 : Double.parseDouble(balance);
        if (inputBalance > total){
            showToast("转出金额超限");
            return;
        }
        remote(input);
    }

    private void balance(){
        showProgressDialog("获取数据中...");
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
                        dismissProgressDialog();
                        DebugUtil.d("IndexActivity 获取余额失败：" + e.getMessage());
                        result();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        DebugUtil.d("IndexActivity 获取余额成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            balance = jsonObject.get("data").getAsString();
                        }
                        result();
                    }
                });
    }

    private void result(){
        enableBalanceView.setText(String.format("可用余额 %s元", TextUtils.isEmpty(balance) ? "0" : LocaleUtil.formatMoney(balance)));
    }

    private void remote(String withdraw){
        showProgressDialog("申请中...");
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .withdraw(withdraw)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        showToast("请求失败，请检查网络");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        DebugUtil.d("WithdrawalActivity 转出申请：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            showSuccessDialog();
                        }else {
                            showToast("转出申请失败，请重试");
                        }
                    }
                });
    }

    private void showSuccessDialog(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("转出申请成功")
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }
}
