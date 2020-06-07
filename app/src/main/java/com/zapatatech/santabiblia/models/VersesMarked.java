package com.zapatatech.santabiblia.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.TreeMap;

public class VersesMarked implements Parcelable {
    private Book book;
    private Label label;
    private int chapter;
    private String note;
    private String uuid;
//    protected List<String> textsGroup = new ArrayList();
    protected TreeMap<Integer, String> verseTextDict = new TreeMap<>();
    public VersesMarked(String uuid, Book book, Label label, int chapter, int verse, String text, String note) {
        this.uuid = uuid;
        this.book = book;
        this.label = label;
        this.chapter = chapter;
        this.note = note;
//        this.verse = verse;
//        this.text = text;
        verseTextDict.put(verse, text);
    }

    public void addToVerseTextDict(int verse, String text){
        this.verseTextDict.put(verse, text);
    }
    public String getUuid() {
        return uuid;
    }
    public TreeMap<Integer, String> getVerseTextDict(){
        return this.verseTextDict;
    }
//    public int getIdVerseMarked() {
//        return this.id;
//    }
    public Book getBook() {
        return book;
    }
    public Label getLabel() {
        return label;
    }
    public int getChapter() {
        return chapter;
    }
    public boolean hasNote() {
        return (this.note == null || this.note.equals("")) ? false : true;
    }
    public String getNote() {
        return this.note;
    }


    //PARCELABLE===========================================================================
    public static final Creator<VersesMarked> CREATOR = new Creator<VersesMarked>() {
        public VersesMarked createFromParcel(Parcel in) {
            return new VersesMarked(in);
        }
        public VersesMarked[] newArray(int size) {
            return new VersesMarked[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.book, flags);
        dest.writeParcelable(this.label, flags);
        dest.writeSerializable(this.verseTextDict);
        dest.writeString(this.uuid);
        dest.writeInt(this.chapter);
        dest.writeString(this.note);
    }

    public VersesMarked(Parcel in) {
        this.book = (Book) in.readParcelable(Book.class.getClassLoader());
        this.label = (Label) in.readParcelable(Label.class.getClassLoader());
        this.verseTextDict = (TreeMap<Integer, String>) in.readSerializable();
        this.uuid = in.readString();
        this.chapter = in.readInt();
        this.note = in.readString();
    }
}
