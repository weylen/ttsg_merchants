package com.strangedog.weylen.mthc.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.IndexActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements LoginView {

    @Bind(R.id.account) EditText mAccountView;
    @Bind(R.id.password) EditText mPasswordView;
    @Bind(R.id.containerView) LinearLayout containerView;
    @Bind(R.id.layout_user) TextInputLayout mLayoutUser;
    @Bind(R.id.layout_password) TextInputLayout mLayoutPassword;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.LoginText));
        ButterKnife.bind(this);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mAccountView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_NEXT) {
                mPasswordView.requestFocus();
                return true;
            }
            return false;
        });

        loginPresenter = new LoginPresenter(this);
        setPresenter(loginPresenter);

        // 测试 模拟数据
        mAccountView.setText("18108037736");
        mPasswordView.setText("123456");
    }

    @OnClick(R.id.account_sign_in_button)
    void onLogin() {
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
        finish();
//        attemptLogin();
    }

    /**
     * 登录
     */
    private void attemptLogin() {
        String account = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (TextUtils.isEmpty(account)) {
            showSnakeBar(containerView, "用户名必须输入");
            mLayoutUser.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showSnakeBar(containerView, "密码必须输入");
            mLayoutPassword.requestFocus();
            return;
        }
        loginPresenter.login(account, password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void showWaitDialog() {
        showProgressDialog("登录中...");
    }

    @Override
    public void dismissWaitDialog() {
        dismissProgressDialog();
    }

    @Override
    public void loginSuccess() {
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFailed() {
        showSnakeBar(containerView, "登录失败，请检查用户名或密码");
    }

    @Override
    public void setPresenter(LoginPresenter presenter) {

    }
}

