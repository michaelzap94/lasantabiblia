package com.zapatatech.santabiblia.models;

public class User {
    private int user_id;
    private String email;
    private String fullname;
    private String account_type;
    private String social_id;

    public User(int user_id, String email, String fullname, String account_type, String social_id) {
        this.user_id = user_id;
        this.email = email;
        this.fullname = fullname;
        this.account_type = account_type;
        this.social_id = social_id;
    }

    public int getUserId() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return fullname;
    }

    public String getAccountType() {
        return account_type;
    }

    public String getSocialId() {
        return social_id;
    }
}