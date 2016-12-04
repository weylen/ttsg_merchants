package com.strangedog.weylen.mthc.http;

import java.util.HashMap;

/**
 * Created by weylen on 2016-07-20.
 */
public class Constants {

    public static final String DEVICE_TOKEN = "2";

    // 正式网
    public static final String BASE_URL = "http://s-280082.gotocdn.com/";
    // 测试网
//    public static final String BASE_URL = "http://s-339005.abc188.com";

    public static final int REQUEST_COUNT = 20; // 每一页的数量

    public static final String EMPTY_STR = new String();

    public static HashMap<String, String> ORDER_PARAM = null;

    public static final String TIME_START = " 00:00:00";
    public static final String TIME_END = " 23:59:59";

    static {
        ORDER_PARAM = new HashMap<>();
        ORDER_PARAM.put("1", "订单已完成");
        ORDER_PARAM.put("2", "等待支付");
        ORDER_PARAM.put("3", "等待接单");
        ORDER_PARAM.put("4", "退单");
        ORDER_PARAM.put("5", "订单已取消");
        ORDER_PARAM.put("6", "支付确认中");
        ORDER_PARAM.put("7", "配送中");
        ORDER_PARAM.put("8", "已送达，等待客户确认收货");
    }
}
