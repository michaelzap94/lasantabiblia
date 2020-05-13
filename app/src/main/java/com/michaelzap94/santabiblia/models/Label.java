package com.michaelzap94.santabiblia.models;

public class Label {
    private String name;
    private String color;
    private int id;
    private int permanent;

    public Label(int id, String name, String color, int permanent) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.permanent = permanent;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPermanent() {
        return permanent;
    }
    public void setPermanent(int permanent) {
        this.permanent = permanent;
    }
}
