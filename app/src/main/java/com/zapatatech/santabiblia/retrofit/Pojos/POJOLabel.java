package com.zapatatech.santabiblia.retrofit.Pojos;

public class POJOLabel {
    private int _id;
    private String name;
    private String color;
    private int permanent;
    private int state;

    public POJOLabel(int _id, String name, String color, int permanent, int state) {
        this._id = _id;
        this.name = name;
        this.color = color;
        this.permanent = permanent;
        this.state = state;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getPermanent() {
        return permanent;
    }

    public int getState() {
        return state;
    }
}
