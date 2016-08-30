package com.strangedog.weylen.mthc.entity;

/**
 * Created by Administrator on 2016-07-03.
 */
public class SalesEntity {

    private String id; // id
    private String sumprice; // 价格
    private String sumamount; // 数量
    private String name; // 名字
    private String img; // 图片

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSumprice() {
        return sumprice;
    }

    public void setSumprice(String sumprice) {
        this.sumprice = sumprice;
    }

    public String getSumamount() {
        return sumamount;
    }

    public void setSumamount(String sumamount) {
        this.sumamount = sumamount;
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
}
