package com.strangedog.weylen.mthc.activity.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.strangedog.weylen.mthc.BasePresenter;
import com.google.common.base.Preconditions;
import com.strangedog.weylen.mthc.entity.AccountEntity;
import com.strangedog.weylen.mthc.http.Constants;
import com.strangedog.weylen.mthc.http.HttpService;
import com.strangedog.weylen.mthc.http.ResponseMgr;
import com.strangedog.weylen.mthc.http.RetrofitFactory;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DeviceUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-20.
 */
public class LoginPresenter implements BasePresenter{

    private LoginView loginView;

    public LoginPresenter(@NonNull LoginView loginView){
       this.loginView = Preconditions.checkNotNull(loginView, "LoginView can not be null");
    }

    @Override
    public void start() {

    }

    /**
     * 登录
     * @param user
     * @param pwd
     */
    public void login(Context context, String user, String pwd){
        loginView.showWaitDialog();
        String uuid = DeviceUtil.INSTANCE.getDeviceUuid(context);
        DebugUtil.d("LoginPresenter 设备号：" + uuid);
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .login(user, pwd, Constants.DEVICE_TOKEN, DeviceUtil.INSTANCE.getDeviceUuid(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("登录异常" + e.getMessage());
                        loginView.dismissWaitDialog();
                        loginView.loginFailed();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("LoginPresenter onNext s:" + s);
                        parseLoginData(s);
                        loginView.dismissWaitDialog();
                    }
                });
    }

    /**
     * 解析登录数据
     * @param source
     */
    private void parseLoginData(JsonObject source){
        int status = ResponseMgr.getStatus(source);
        DebugUtil.d("LoginPresenter parseLoginData status:" + status);
        if (status != 1){
            loginView.loginFailed();
        }else{
            Gson gson = new Gson();
            AccountEntity entity = gson.fromJson(ResponseMgr.getData(source), AccountEntity.class);
            // 保存登录信息
            LoginData.INSTANCE.setAccountEntity(entity);
            loginView.loginSuccess();

            DebugUtil.d("LoginPresenter parseLoginData 登录信息：" + entity);
        }
    }
}
