package com.michaelzap94.santabiblia.objects;

public class Book {
    public static final int OLD_TESTAMENT = 1;
    public static final int NEW_TESTAMENT = 2;
    private int id;
    private String name;
    private int numcap;

    public Book(int id, String name, int numcap) {
        this.id = id;
        this.name = name;
        this.numcap = numcap;
    }

    public int getId() {
        return this.id;
    }

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
