package com.strangedog.weylen.mthc.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.strangedog.weylen.mtch.R;
import com.strangedog.weylen.mthc.BaseActivity;
import com.strangedog.weylen.mthc.BaseApplication;
import com.strangedog.weylen.mthc.activity.login.LoginActivity;
import com.strangedog.weylen.mthc.activity.login.LoginData;
import com.strangedog.weylen.mthc.activity.order.IndexActivity;
import com.strangedog.weylen.mthc.activity.orderdetails.OrderDetailsActivity;
import com.strangedog.weylen.mthc.util.DebugUtil;
import com.strangedog.weylen.mthc.util.DeviceUtil;
import com.strangedog.weylen.mthc.util.MediaUtil;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;
import java.util.Random;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 MiMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.xiaomi.mipushdemo.MiMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、MiMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、MiMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、MiMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、MiMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、MiMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 */
public class MiMessageReceiver extends PushMessageReceiver {

    public static final int LOGIN_ID = 4001;
    public static final int USER_CONFIRM_ID = 4002;
    public static final int NEW_ORDER_ID = 4003;

    @Override // 接收服务器向客户端发送的透传消息
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        DebugUtil.d("onReceivePassThroughMessage is called. " + message.toString());
        doMessage(context, message);
    }

    @Override // 来接收服务器向客户端发送的通知消息， 这个回调方法会在用户手动点击通知后触发
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        DebugUtil.d("onNotificationMessageClicked is called. " + message.toString());
    }

    @Override // 方法用来接收服务器向客户端发送的通知消息，这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        DebugUtil.d("onNotificationMessageArrived is called. " + message.toString());
//        doMessage(context, message);
    }

    @Override  // 方法用来接收客户端向服务器发送命令后的响应结果。
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        DebugUtil.d("onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String mAlias;
        if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                DebugUtil.d("MiMessageReceiver 设置别名：" + mAlias);
            }
        }if (MiPushClient.COMMAND_UNSET_ALIAS.equalsIgnoreCase(command)){
            if (message.getResultCode() == ErrorCode.SUCCESS){
                DebugUtil.d("MiMessageReceiver 取消别名：" + cmdArg1);
            }
        }
    }

    @Override // 方法用来接收客户端向服务器发送注册命令后的响应结果
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        DebugUtil.d("onReceiveRegisterResult is called." + message.toString());
        String command = message.getCommand(); // 获取注册的种类

        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log = cmdArg1;
        // 注册服务
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                MiPushClient.setAlias(context, DeviceUtil.INSTANCE.getDeviceUuid(context), null);
            }
        } else {
            log = message.getReason();
        }
        DebugUtil.d("MiMessageReceiver 注册服务：" + log);
    }

    /**
     * 显示异地登录框
     * @param context
     */
    private void showAnotherPlaceDialog(Context context){
        Handler handler = new Handler(context.getMainLooper());
        handler.post(() -> {
            AlertDialog dialog = new AlertDialog.Builder(BaseActivity.getCurrentContext())
                    .setTitle("提示")
                    .setMessage("您的账号在另外一台设备上登录，如果不是本人操作，请您尽快修改密码")
                    .setNegativeButton("退出", (dialog1, which) -> {
                        BaseApplication.exit();
                        dialog1.dismiss();
                    })
                    .setPositiveButton("重新登录", (dialog1, which) -> {
                        dialog1.dismiss();
                        reLogin(context);
                    })
                    .create();
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        });
    }

    private void doMessage(Context context, MiPushMessage message){
        String content = message.getContent();
        Gson gson = new Gson();
        int status = -1;
        try{
            JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
            status = jsonObject.get("stauts").getAsInt();
            DebugUtil.d("MiMessageReceiver status:" + status);
            switch (status){
                case 1: // 异地登录
                    // 判断当前程序是否在前端执行 如果在则显示对话框 如果不在则弹出通知
                    if (!LoginData.INSTANCE.isLogin(context)){
                        DebugUtil.d("MiMessageReceiver 异地登陆通知 用户未登录 不进行通知展示");
                        return;
                    }
                    LoginData.INSTANCE.logout(context);
                    if (DeviceUtil.isRunning(context)){
                        DebugUtil.d("MiMessageReceiver app正在运行");
                        showAnotherPlaceDialog(context);
                    }else {
                        DebugUtil.d("MiMessageReceiver app没有运行");
                        showAnotherNf(context);
                    }
                    break;
                case 2: // 新订单
                    showNewOrderNf(context);
                    break;
                case 5: // 用户确认收货
                    String orderId = jsonObject.get("data").getAsString();
                    showConfirmGoodsNf(context, orderId);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            // 如果弹出对话框失败， 就直接跳转到登录页面
            if (status == 1){
                reLogin(context);
            }
        }
    }

    /**
     * 重新登录
     * @param context
     */
    private void reLogin(Context context){
        LoginData.INSTANCE.logout(context);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 显示新订单的通知消息
     */
    private void showNewOrderNf(Context context){
        if (!LoginData.INSTANCE.isLogin(context)){
            DebugUtil.d("MiMessageReceiver showReceiveOrder 用户未登录 不进行通知展示");
            return;
        }
        Intent intent = new Intent(context, IndexActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String ticker = "您有新订单";
        String message = "您有新订单，点击立即查看";
        showNf(context, ticker, message, intent, NEW_ORDER_ID);
        if (BaseApplication.INSTANCE != null){
            MediaUtil.paly(BaseApplication.INSTANCE);
        }else {
            MediaUtil.paly(context);
        }
    }

    /**
     * 显示异地登录的通知消息
     */
    private void showAnotherNf(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String ticker = "您的账号在另外一台设备上登录，如果不是本人操作，请您尽快修改密码";
        showNf(context, ticker, ticker, intent, LOGIN_ID);
    }

    /**
     * 显示客户收货的通知
     * @param context
     * @param orderId
     */
    private void showConfirmGoodsNf(Context context, String orderId){
        if (!LoginData.INSTANCE.isLogin(context)){
            DebugUtil.d("MiMessageReceiver showConfirmGoodsNf 用户未登录 不进行通知展示");
            return;
        }
        Intent intent = new Intent(context, OrderDetailsActivity.class);
        intent.putExtra(OrderDetailsActivity.ORDER_KEY, orderId);
        String ticker = "您的订单" + orderId + "已确认收货";
        String message = "您的订单" + orderId + "已确认收货，点击查看详情";
        showNf(context, ticker, message, intent, USER_CONFIRM_ID);
    }

    private void showNf(Context context, String ticker, String message, Intent intent, int notifyId){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.icon_message);
        builder.setTicker(ticker);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher_icon));
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("天天闪购提示您");
        builder.setContentText(message);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notifyId, notification);
    }
}
