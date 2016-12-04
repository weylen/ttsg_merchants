package com.strangedog.weylen.mthc.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016-07-03.
 */
public class ProductsEntity implements Parcelable{

    private String id; // 商品id
    private String name; // 商品名字
    private String standard; // 商品规格
    private String kind; // 商品种类
    private String imgPath; // 商品展示图片地址，如果是多个逗号隔开
    private String buyPrice; // 商品进价
    private String salePrice; // 商品售价
    private String stock; // 库存
    private String info; // 促销信息
    private String promote; // 促销价格
    private String begin; // 促销开始时间
    private String end; // 促销结束时间
    private String stauts; // 状态 1是上架 2是下架
    private boolean isTop; // 是否置顶

    public ProductsEntity() {
    }

    protected ProductsEntity(Parcel in) {
        id = in.readString();
        name = in.readString();
        standard = in.readString();
        kind = in.readString();
        imgPath = in.readString();
        buyPrice = in.readString();
        salePrice = in.readString();
        stock = in.readString();
        info = in.readString();
        promote = in.readString();
        begin = in.readString();
        end = in.readString();
        stauts = in.readString();
        isTop = in.readInt() == 1;
    }

    public static final Creator<ProductsEntity> CREATOR = new Creator<ProductsEntity>() {
        @Override
        public ProductsEntity createFromParcel(Parcel in) {
            return new ProductsEntity(in);
        }

        @Override
        public ProductsEntity[] newArray(int size) {
            return new ProductsEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPromote() {
        return promote;
    }

    public void setPromote(String promote) {
        this.promote = promote;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStauts() {
        return stauts;
    }

    public void setStauts(String stauts) {
        this.stauts = stauts;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(standard);
        dest.writeString(kind);
        dest.writeString(imgPath);
        dest.writeString(buyPrice);
        dest.writeString(salePrice);
        dest.writeString(stock);
        dest.writeString(info);
        dest.writeString(promote);
        dest.writeString(begin);
        dest.writeString(end);
        dest.writeString(stauts);
        dest.writeInt(isTop? 1 : 0);
    }
}
