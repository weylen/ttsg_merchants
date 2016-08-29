package com.strangedog.weylen.mthc.activity.withdrawal;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged(value = R.id.inputBalance, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChanged(CharSequence text){
        DebugUtil.d("WithdrawalActivity 输入的文本：" + text);
        if (TextUtils.isEmpty(text)){
            confirmWithdrawalView.setEnabled(false);
        }else {
            confirmWithdrawalView.setEnabled(true);
        }
    }

    // 点击全部提现
    @OnClick(R.id.text_all_balance)
    void onAllBalanceClick(){
        balanceEdit.setText(balance);
    }

    /**
     * 点击全部提现
     */
    @OnClick(R.id.confirm_withdrawal)
    void onConfirmDrawablClick(){

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
        enableBalanceView.setText(String.format("可用余额 %s元", balance == null ? "0" : balance));
    }
}
