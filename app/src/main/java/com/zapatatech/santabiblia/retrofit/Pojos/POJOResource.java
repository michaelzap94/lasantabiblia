package com.zapatatech.santabiblia.retrofit.Pojos;

public class POJOResource {
    private int id;
    private String url;
    private String name;
    private String resource_type;
    private String language;
    private String description;
    private int version;
    private String filename;
    private int size;
    private String resource;
    private String state;
    private int progress;

    public POJOResource(int id, String url, String name, String resource_type, String language, String description, int version, String filename, int size, String resource) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.resource_type = resource_type;
        this.language = language;
        this.description = description;
        this.version = version;
        this.filename = filename;
        this.size = size;
        this.resource = resource;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getResource_type() {
        return resource_type;
    }

    public String getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    public int getVersion() {
        return version;
    }

    public String getFilename() {
        return filename;
    }

    public int getSize() {
        return size;
    }

    public String getResource() {
        return resource;
    }

    public void setTemporalState(String state){this.state = state;}
    public void setTemporalProgress(int progress){this.progress = progress;}
    public String getTemporalState(){return this.state;}
    public int getTemporalProgress(){return this.progress;}
}
