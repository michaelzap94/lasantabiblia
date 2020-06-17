package com.zapatatech.santabiblia.models;


public class SyncUp {
    private String email;
    private int version;
    private int state;
    private String updated;

    public SyncUp(String email, int version, int state, String updated) {
        this.email = email;
        this.version = version;
        this.state = state;
        this.updated = updated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
