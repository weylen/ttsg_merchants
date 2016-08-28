package com.strangedog.weylen.mthc.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderDetailsEntity implements Serializable{

    private String orderId;
    private String total;
    private String prepay_id;
    private ArrayList<OrderDetailsProductsEntity> products;

    public OrderDetailsEntity(){}

    public OrderDetailsEntity(String orderId, String total, String prepay_id, ArrayList<OrderDetailsProductsEntity> products) {
        this.orderId = orderId;
        this.total = total;
        this.prepay_id = prepay_id;
        this.products = products;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<OrderDetailsProductsEntity> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<OrderDetailsProductsEntity> products) {
        this.products = products;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }
}
