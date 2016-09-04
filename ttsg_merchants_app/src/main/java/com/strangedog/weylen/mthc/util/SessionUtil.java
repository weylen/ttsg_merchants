package com.strangedog.weylen.mthc.util;

import android.text.TextUtils;

import com.strangedog.weylen.mthc.entity.AccountEntity;
import com.strangedog.weylen.mthc.activity.login.LoginData;

/**
 * Created by weylen on 2016-07-28.
 */
public class SessionUtil {
    public static String sessionId = null;

    public static String getSessionId(){
        if (TextUtils.isEmpty(sessionId)){
            AccountEntity accountEntity = LoginData.INSTANCE.getAccountEntity();
            if (accountEntity == null || accountEntity.getSid() == null){
                return "";
            }
            sessionId = "JSESSIONID=" + accountEntity.getSid();
        }
        return sessionId;
    }
}
