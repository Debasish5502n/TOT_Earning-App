package com.friends.tot_earning.Model;

public class User {
    private String name, email,profile, referCode;
    private long coins;
    private long totalCoins,date,illegalActivity;
    private String phone,uid,unBanned;

    public User() {
    }

    public User(String name, String email,String profile, String referCode,long coin,long totalCoins,String phone,String uid,Long date,Long illegalActivity,String unBanned) {
        this.name = name;
        this.email = email;
        this.referCode = referCode;
        this.coins =coin;
        this.totalCoins =totalCoins;
        this.phone = phone;
        this.profile = profile;
        this.uid = uid;
        this.date = date;
        this.illegalActivity = illegalActivity;
        this.unBanned = unBanned;
    }

    public String getUnBanned() {
        return unBanned;
    }

    public void setUnBanned(String unBanned) {
        this.unBanned = unBanned;
    }

    public long getIllegalActivity() {
        return illegalActivity;
    }

    public void setIllegalActivity(long illegalActivity) {
        this.illegalActivity = illegalActivity;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public long getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(long totalCoins) {
        this.totalCoins = totalCoins;
    }
}
