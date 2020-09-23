package com.zapatatech.santabiblia.retrofit.Pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class POJONote implements Parcelable {
    private int user;
    private String _id;
    private String label_id;
    private String title;
    private String content;
    private String date_created;
    private String date_updated;
    private int state;

    public POJONote(int user, String _id, String label_id, String title, String content, String date_created, String date_updated, int state) {
        this.user = user;
        this._id = _id;
        this.label_id = label_id;
        this.title = title;
        this.content = content;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.state = state;
    }

    public int getUserId() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String get_id() {
        return _id;
    }

    public String getLabel_id() {
        return label_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public int getState() {
        return state;
    }

    //PARCELABLE===========================================================================
    public static final Parcelable.Creator<POJONote> CREATOR = new Parcelable.Creator<POJONote>() {
        public POJONote createFromParcel(Parcel in) {
            return new POJONote(in);
        }
        public POJONote[] newArray(int size) {
            return new POJONote[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.user);
        dest.writeString(this._id);
        dest.writeString(this.label_id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.date_created);
        dest.writeString(this.date_updated);
        dest.writeInt(this.state);
    }
    private POJONote(Parcel in) {
        this.user = in.readInt();
        this._id = in.readString();
        this.label_id = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.date_created = in.readString();
        this.date_updated = in.readString();
        this.state = in.readInt();
    }
}
