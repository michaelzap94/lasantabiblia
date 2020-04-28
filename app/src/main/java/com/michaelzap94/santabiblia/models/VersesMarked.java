package com.michaelzap94.santabiblia.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.michaelzap94.santabiblia.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class VersesMarked implements Parcelable {
    public static final Creator<VersesMarked> CREATOR = new Creator<VersesMarked>() {
        public VersesMarked createFromParcel(Parcel in) {
            return new VersesMarked(in);
        }

        public VersesMarked[] newArray(int size) {
            return new VersesMarked[size];
        }
    };
    private int id;
    private Book book;
    private Label label;
    private int chapter;
    private String note;
    private String uuid;
//    protected List<String> textsGroup = new ArrayList();
    protected TreeMap<Integer, String> verseTextDict = new TreeMap<>();


    public VersesMarked(int id, String uuid, Book book, Label label, int chapter, int verse, String text, String note) {
        this.id = id;
        this.uuid = uuid;
        this.book = book;
        this.label = label;
        this.chapter = chapter;
        this.note = note;
//        this.verse = verse;
//        this.text = text;
        verseTextDict.put(verse, text);
    }

    public VersesMarked(Parcel in) {
        this.id = in.readInt();
        this.note = in.readString();
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
    public int getIdVerseMarked() {
        return this.id;
    }
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
        return (this.note == null || this.note.equals(BuildConfig.FLAVOR)) ? false : true;
    }
    public String getNote() {
        return this.note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
