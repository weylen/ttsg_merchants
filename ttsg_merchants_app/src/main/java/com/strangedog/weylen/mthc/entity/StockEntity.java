package com.strangedog.weylen.mthc.entity;

/**
 * Created by weylen on 2016-08-29.
 */
public class StockEntity {

    // {"name"：商品名称,"standard"：商品规格,"kind"：商品种类,"img"：图片id,"price"：商品价格,"amount"：现有库存}

    private String name;
    private String standard;
    private String kind;
    private String img;
    private String price;
    private String amount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
