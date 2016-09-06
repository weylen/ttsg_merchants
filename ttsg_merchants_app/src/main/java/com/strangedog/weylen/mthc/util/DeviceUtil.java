package com.strangedog.weylen.mthc.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhou on 2016/9/2.
 */
public enum DeviceUtil {

    INSTANCE;

    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected static UUID uuid;

    private void create(Context context) {
        synchronized (DeviceUtil.class) {
            if(uuid == null) {
                final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                final String id = prefs.getString(PREFS_DEVICE_ID, null );
                if (id != null) {
                    // Use the ids previously computed and stored in the prefs file
                    uuid = UUID.fromString(id);
                } else {
                    final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    // Use the Android ID unless it's broken, in which case fallback on deviceId,
                    // unless it's not available, then fallback on a random number which we store
                    // to a prefs file
                    try {
                        if (!"9774d56d682e549c".equals(androidId)) {
                            uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                        } else {
                            final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE )).getDeviceId();
                            uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    // Write the value out to the prefs file
                    prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                }
            }
        }
    }

    public String getDeviceUuid(Context context) {
        if (uuid == null){
            create(context);
        }
        return uuid.toString();
    }

    public static boolean isRunning(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        String MY_PKG_NAME = context.getPackageName();
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }
}
