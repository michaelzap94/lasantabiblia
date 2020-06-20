package com.zapatatech.santabiblia.retrofit.Pojos;

public class POJOVersesMarked {
    private int user;
    private int _id;
    private int label_id;
    private int book_number;
    private int chapter;
    private int verseFrom;
    private int verseTo;
    private String label_name;
    private String label_color;
    private int label_permanent;
    private String note;
    private String date_created;
    private String date_updated;
    private String UUID;
    private int state;

    public POJOVersesMarked(int user, int _id, int label_id, int book_number, int chapter, int verseFrom, int verseTo, String label_name, String label_color, int label_permanent, String note, String date_created, String date_updated, String UUID, int state) {
        this.user = user;
        this._id = _id;
        this.label_id = label_id;
        this.book_number = book_number;
        this.chapter = chapter;
        this.verseFrom = verseFrom;
        this.verseTo = verseTo;
        this.label_name = label_name;
        this.label_color = label_color;
        this.label_permanent = label_permanent;
        this.note = note;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.UUID = UUID;
        this.state = state;
    }

    public int getUserId() {
        return user;
    }


    public int get_id() {
        return _id;
    }

    public int getLabel_id() {
        return label_id;
    }

    public int getBook_number() {
        return book_number;
    }

    public int getChapter() {
        return chapter;
    }

    public int getVerseFrom() {
        return verseFrom;
    }

    public int getVerseTo() {
        return verseTo;
    }

    public String getLabel_name() {
        return label_name;
    }

    public String getLabel_color() {
        return label_color;
    }

    public int getLabel_permanent() {
        return label_permanent;
    }

    public String getNote() {
        return note;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public String getUUID() {
        return UUID;
    }

    public int getState() {
        return state;
    }
}
