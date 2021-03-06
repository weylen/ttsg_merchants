package com.strangedog.weylen.mthc.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.strangedog.weylen.mthc.entity.ProductsEntity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by weylen on 2016-08-03.
 */
public class LocaleUtil {

    /**
     * 是否有促销信息
     * @param promotionPrice
     * @return true 有促销
     */
    public static boolean hasPromotion(String promotionPrice, String endTime){
        if (!TextUtils.isEmpty(promotionPrice)){
            try{
                double d = Double.valueOf(promotionPrice);
                if (d > 0){ // 促销价格>0的时候 再检查时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(endTime);
                    if (System.currentTimeMillis() >= date.getTime()){
                        return false;
                    }
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean isLessThanZero(String str){
        if (TextUtils.isEmpty(str)){
            return false;
        }
        try {
            double d = Double.parseDouble(str);
            if (d < 0){
                return true;
            }
        }catch (Exception e){

        }
        return false;
    }

    /**
     * 转换下架和上架的字符串
     * @param status
     * @param data
     * @return
     */
    public static String formatUpDownStr(int status, List<ProductsEntity> data){
        StringBuilder builder = new StringBuilder();
        String line = new String("-");
        String comma = new String(",");
        for (ProductsEntity entity : data){
            builder.append(status);
            builder.append(line);
            builder.append(entity.getId());
            builder.append(comma);
        }
        String param = builder.toString();
        if (param.endsWith(",")){
            param = param.substring(0, param.length() - 1);
        }
        return param;
    }

    public static final boolean isListEmpty(List<?> data){
        return data == null || data.size() == 0;
    }

    public static String formatMoney(String money){
        if (TextUtils.isEmpty(money)){
            return money;
        }
        BigDecimal bigDecimal = new BigDecimal(money);
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 判断网络是否可用
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context){
        boolean isConnFlag = false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network != null){
            isConnFlag = conManager.getActiveNetworkInfo().isAvailable();
        }
        return isConnFlag;
    }
}
