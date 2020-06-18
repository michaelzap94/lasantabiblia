package com.zapatatech.santabiblia.models;

import android.text.Spanned;

import com.zapatatech.santabiblia.utilities.BookHelper;

public class SearchResult {
    private int _id;
    private String title;
    private Spanned contentSpanned;
    private int book_number;
    private int chapter_number;
    private int verse;
    private boolean isClickable;

    public SearchResult(int _id, String title, Spanned contentSpanned) {
        this._id = _id;
        this.title = title;
        this.contentSpanned = contentSpanned;
        this.isClickable = false;
    }

    public SearchResult(int book_number, int chapter_number, int verse, Spanned text) {
        this.book_number = book_number;
        this.chapter_number = chapter_number;
        this.verse = verse;
        this.title = BookHelper.getBook(book_number).getName() + " " + chapter_number + ":" + verse;
        this.contentSpanned = text;
        this.isClickable = true;
    }

    public boolean getIsClickable() {
        return isClickable;
    }
    public int get_id() {
        return _id;
    }
    public String getTitle() {
        return title;
    }
    public Spanned getContent() {
        return contentSpanned;
    }
    public int getBook_number() {
        return book_number;
    }
    public int getChapter_number() {
        return chapter_number;
    }
    public int getVerse() {
        return verse;
    }

}
