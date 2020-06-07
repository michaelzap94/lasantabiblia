package com.zapatatech.santabiblia.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    public static final int OLD_TESTAMENT = 1;
    public static final int NEW_TESTAMENT = 2;
    private int id;
    private String name;
    private int numcap;
    private int book_number;
    private String shortName;

    public Book(int id, int book_number, String name, int numcap) {
        this.id = id;
        this.book_number = book_number;
        this.name = name;
        this.numcap = numcap;
    }

    public Book(int id, int book_number, String name, String shortName, int numcap) {
        this.id = id;
        this.book_number = book_number;
        this.name = name;
        this.shortName = shortName;
        this.numcap = numcap;
    }

    public String getShortName() {
        return this.shortName;
    }

    public int getId() {
        return this.id;
    }

    public int getBookNumber() { return  this.book_number; }

    public String getName() {
        return this.name;
    }

    public int getType() {
        return this.id < 40 ? OLD_TESTAMENT : NEW_TESTAMENT;
    }

    public int getNumCap() {
        return this.numcap;
    }

    //PARCELABLE===========================================================================
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.book_number);
        dest.writeString(this.name);
        dest.writeInt(this.numcap);
    }
    private Book(Parcel in) {
        this.id = in.readInt();
        this.book_number = in.readInt();
        this.name = in.readString();
        this.numcap = in.readInt();
    }
}
