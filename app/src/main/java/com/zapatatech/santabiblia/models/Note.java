package com.zapatatech.santabiblia.models;

public class Note {
    private String id;
    private String title;
    private String content;

    public Note(){}
    public Note(String id, String title,String content){
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
