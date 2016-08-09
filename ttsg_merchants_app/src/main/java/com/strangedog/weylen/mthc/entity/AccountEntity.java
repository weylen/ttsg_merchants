package com.strangedog.weylen.mthc.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weylen on 2016-07-21.
 */
public class AccountEntity implements Parcelable{

    private String account; // 支付或结算账号
    private String area; // 区域代码
    private String id;
    private String name; // 用户姓名
    private String shoper; // 店铺名称
    private String sid; // sessionId
    private String uname; // 用户登录名称(电话号码)

    protected AccountEntity(Parcel in) {
        account = in.readString();
        area = in.readString();
        id = in.readString();
        name = in.readString();
        shoper = in.readString();
        sid = in.readString();
        uname = in.readString();
    }

    public AccountEntity(){}

    public static final Creator<AccountEntity> CREATOR = new Creator<AccountEntity>() {
        @Override
        public AccountEntity createFromParcel(Parcel in) {
            return new AccountEntity(in);
        }

        @Override
        public AccountEntity[] newArray(int size) {
            return new AccountEntity[size];
        }
    };

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

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

    public String getShoper() {
        return shoper;
    }

    public void setShoper(String shoper) {
        this.shoper = shoper;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "account='" + account + '\'' +
                ", area='" + area + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shoper='" + shoper + '\'' +
                ", sid='" + sid + '\'' +
                ", uname='" + uname + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(account);
        dest.writeString(area);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(shoper);
        dest.writeString(sid);
        dest.writeString(uname);
    }
}
