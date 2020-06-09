package com.zapatatech.santabiblia.models;

public class AuthInfo {
    private String access;
    private String refresh;
    private String status;
    private String detail;
    private Object error;

    public AuthInfo(String access, String refresh, String status, String detail, String error) {
        this.access = access;
        this.refresh = refresh;
        this.status = status;
        this.detail = detail;
        this.error = error;
    }

    public String getAccessToken(){
        return access;
    }
    public String getRefreshToken(){
        return refresh;
    }
    public String getStatus() {
        return status;
    }
    public Object getError() {
        return error;
    }
    public String getDetail() { return detail; }
}
