package com.prm392.quizgame.model;

import com.google.type.DateTime;

import java.util.Date;

public class User {
    private String name;
    private String email;
    private String password;
    private long changeSpin;
    private Date lastSpinTime;
    private long coins = 00;
    private boolean isPremium = false;

    public User() {
    }

    public User(String name, String email, String password, long changeSpin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.changeSpin = changeSpin;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public boolean isPremium() {
        return true;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
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

    public long getChangeSpin() {
        return changeSpin;
    }

    public void setChangeSpin(long changeSpin) {
        this.changeSpin = changeSpin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public Date getLastSpinTime() {
        return lastSpinTime;
    }

    public void setLastSpinTime(Date lastSpinTime) {
        this.lastSpinTime = lastSpinTime;
    }
}
