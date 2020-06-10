package com.zapatatech.santabiblia.models;

public class User {
    private String user_id;
    private String email;
    private String firstname;
    private String lastname;

    public User(String user_id, String email, String firstname, String lastname) {
        this.user_id = user_id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstname;
    }

    public String getLastName() {
        return lastname;
    }
}