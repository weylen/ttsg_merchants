package com.strangedog.weylen.mthc.activity.login;

import android.content.Context;

import com.strangedog.weylen.mthc.BaseApplication;
import com.strangedog.weylen.mthc.entity.AccountEntity;
import com.strangedog.weylen.mthc.prefs.LoginPrefs;

/**
 * Created by weylen on 2016-07-21.
 */
public enum LoginData {

    INSTANCE;

    private AccountEntity accountEntity;

    public AccountEntity getAccountEntity() {
        return getAccountEntity(BaseApplication.INSTANCE);
    }

    public AccountEntity getAccountEntity(Context context){
        if (accountEntity == null){
            accountEntity = LoginPrefs.getAccount(context);
        }
        return accountEntity;
    }

    public void setAccountEntity(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    public boolean isLogin(Context context){
        return getAccountEntity(context) != null;
    }

    public void logout(Context context){
        LoginPrefs.saveAccount(context, null);
        accountEntity = null;
    }
}
