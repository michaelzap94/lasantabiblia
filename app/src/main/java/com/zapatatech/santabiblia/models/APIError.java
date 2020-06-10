package com.zapatatech.santabiblia.models;

public class APIError {

    private int statusCode;
    private String detail;
    private String code;

    public APIError() {
    }

    public int getStatusCode() {
        return statusCode;
    }
    public String getCode() {
        return code;
    }
    public String message() {
        return detail;
    }
}
