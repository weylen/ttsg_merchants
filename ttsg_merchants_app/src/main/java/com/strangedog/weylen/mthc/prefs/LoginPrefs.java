package com.strangedog.weylen.mthc.prefs;

import android.content.Context;

import com.strangedog.weylen.mthc.entity.AccountEntity;
import com.strangedog.weylen.mthc.util.DebugUtil;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by weylen on 2016-08-09.
 */
public class LoginPrefs {

    private static final String DB_NAME = "login_prefs_db";

    public static void saveAccount(Context context, AccountEntity accountEntity){
        try {
            OutputStream os = context.openFileOutput(DB_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream ois = new ObjectOutputStream(os);
            ois.writeObject(accountEntity);
            ois.flush();
            ois.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AccountEntity getAccount(Context context){
        AccountEntity accountEntity = null;
        try {
            InputStream in = context.openFileInput(DB_NAME);
            ObjectInputStream ois = new ObjectInputStream(in);
            accountEntity = (AccountEntity) ois.readObject();
            ois.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountEntity;
    }
}
