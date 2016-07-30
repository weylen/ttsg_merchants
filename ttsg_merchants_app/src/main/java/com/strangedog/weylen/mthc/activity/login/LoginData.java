package com.strangedog.weylen.mthc.activity.login;

import com.strangedog.weylen.mthc.entity.AccountEntity;

/**
 * Created by weylen on 2016-07-21.
 */
public enum LoginData {

    INSTANCE;

    private AccountEntity accountEntity;

    public AccountEntity getAccountEntity() {
        return accountEntity;
    }

    public void setAccountEntity(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }
}
