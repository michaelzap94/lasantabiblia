package com.zapatatech.santabiblia.retrofit.Pojos;

public class POJONote {
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
}
