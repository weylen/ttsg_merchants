package com.strangedog.weylen.mthc.entity;

import java.io.Serializable;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderProductsEntity implements Serializable{
    private String total;
    private String amount;
    private String price;
    private String standard;
    private String promote;
    private String name;
    private String img;
    /**
     "1"："订单完成"
     "2"："订单未支付"
     "3"："订单已支付未发货"
     "4"："客户退货"
     "5"："客户取消订单"
     "6": "支付确认中"
     "7": "商家已接单"
     "8": "商家已送达"

     */
    private String stauts;
    private String date;
    private String orderId;
    private String kind;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getPromote() {
        return promote;
    }

    public void setPromote(String promote) {
        this.promote = promote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    /**
     * "1"："订单完成"
     "2"："订单未支付"
     "3"："订单已支付未发货"
     "4"："客户退货"
     "5"："客户取消订单"
     "6": "支付确认中"
     "7": "商家已接单"
     "8": "商家已送达"
     * @return
     */
    public String getStauts() {
        return stauts;
    }

    public void setStauts(String stauts) {
        this.stauts = stauts;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
