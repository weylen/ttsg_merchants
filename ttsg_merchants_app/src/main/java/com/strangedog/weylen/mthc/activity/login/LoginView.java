package com.strangedog.weylen.mthc.activity.login;

import com.strangedog.weylen.mthc.BaseView;

/**
 * Created by weylen on 2016-07-20.
 */
public interface LoginView extends BaseView<LoginPresenter>{

    void showWaitDialog();
    void dismissWaitDialog();
    void loginSuccess();
    void loginFailed();
}
