package com.michaelzap94.santabiblia.models;

public class Book {
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
}
