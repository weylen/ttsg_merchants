package com.strangedog.weylen.mthc.util;

import android.text.TextUtils;

import com.strangedog.weylen.mthc.entity.ProductsEntity;

import java.math.BigDecimal;
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
    public static boolean hasPromotion(String promotionPrice){
        return (!TextUtils.isEmpty(promotionPrice)) && (!"-1".equalsIgnoreCase(promotionPrice));
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
}
