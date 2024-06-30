package com.yeyunj.teris;

public class UserData {
    private int uid;
    private String account;
    private int max_score;

    public UserData(int uid, String account, int max_score) {
        this.uid = uid;
        this.account = account;
        this.max_score = max_score;
    }

    public int getUid() {
        return uid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getMax_score() {
        return max_score;
    }

    public void setMax_score(int max_score) {
        this.max_score = max_score;
    }
}
