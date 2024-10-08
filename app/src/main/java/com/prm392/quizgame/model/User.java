package com.prm392.quizgame.model;

public class User {
    private String name;
    private String email;
    private String password;
    private String referCode;

    public User() {
    }

    public User(String name, String email, String password, String referCode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.referCode = referCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
