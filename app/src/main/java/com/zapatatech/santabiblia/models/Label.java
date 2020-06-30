package com.zapatatech.santabiblia.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Label  implements Parcelable {
    private String name;
    private String color;
    private String id;
    private int permanent;
    private String uuid;

    public Label(String id, String name, String color, int permanent) {
        this.id = id;
        this.uuid = null;
        this.name = name;
        this.color = color;
        this.permanent = permanent;
    }

    public Label(String id, String name, String color, int permanent, String uuid) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.permanent = permanent;
    }

    public String getUUID() {
        return uuid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getPermanent() {
        return permanent;
    }
    public void setPermanent(int permanent) {
        this.permanent = permanent;
    }

    //PARCELABLE===========================================================================
    public static final Parcelable.Creator<Label> CREATOR = new Parcelable.Creator<Label>() {
        public Label createFromParcel(Parcel in) {
            return new Label(in);
        }
        public Label[] newArray(int size) {
            return new Label[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uuid);
        dest.writeString(this.name);
        dest.writeString(this.color);
        dest.writeInt(this.permanent);
    }
    private Label(Parcel in) {
        this.id = in.readString();
        this.uuid = in.readString();
        this.name = in.readString();
        this.color = in.readString();
        this.permanent = in.readInt();
    }
}
