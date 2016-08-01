package com.strangedog.weylen.mthc.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weylen on 2016-08-01.
 */
public class KindDataEntity implements Parcelable{

    private String id;
    private String pid;
    private String name;

    public KindDataEntity() {
    }

    protected KindDataEntity(Parcel in) {
        id = in.readString();
        pid = in.readString();
        name = in.readString();
    }

    public static final Creator<KindDataEntity> CREATOR = new Creator<KindDataEntity>() {
        @Override
        public KindDataEntity createFromParcel(Parcel in) {
            return new KindDataEntity(in);
        }

        @Override
        public KindDataEntity[] newArray(int size) {
            return new KindDataEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(pid);
        dest.writeString(name);
    }
}
