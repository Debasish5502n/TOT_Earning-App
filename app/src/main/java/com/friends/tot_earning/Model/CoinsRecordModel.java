package com.friends.tot_earning.Model;

public class CoinsRecordModel {

    Long date,coins,plusCoins;
    String source;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getPlusCoins() {
        return plusCoins;
    }

    public void setPlusCoins(Long plusCoins) {
        this.plusCoins = plusCoins;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
