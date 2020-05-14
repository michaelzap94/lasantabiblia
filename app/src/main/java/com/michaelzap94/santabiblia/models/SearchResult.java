package com.michaelzap94.santabiblia.models;

public class SearchResult {
    private int _id;
    private String title;
    private String content;

    public SearchResult(int _id, String title, String content) {
        this._id = _id;
        this.title = title;
        this.content = content;
    }

    public int get_id() {
        return _id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }

}
