package com.michaelzap94.santabiblia.models;

public class Book {
    public static final int OLD_TESTAMENT = 1;
    public static final int NEW_TESTAMENT = 2;
    private int id;
    private String name;
    private int numcap;
    private int tag;
    private String shortName;

    public Book(int id, int tag, String name, int numcap) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.numcap = numcap;
    }

    public Book(int id, int tag, String name, String shortName, int numcap) {
        this.id = id;
        this.tag = tag;
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

    public int getTag() { return  this.tag; }

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
