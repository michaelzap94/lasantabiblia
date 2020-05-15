package com.michaelzap94.santabiblia.models;

import android.text.Spanned;

public class SearchResult {
    private int _id;
    private String title;
    private Spanned contentSpanned;

    public SearchResult(int _id, String title, Spanned contentSpanned) {
        this._id = _id;
        this.title = title;
        this.contentSpanned = contentSpanned;
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

}
