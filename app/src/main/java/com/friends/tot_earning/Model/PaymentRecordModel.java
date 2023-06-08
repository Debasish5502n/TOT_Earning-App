package com.friends.tot_earning.Model;

public class PaymentRecordModel {

    Long date,redeemCoin;
    String number,process,source,sms,documentUid;

    public Long getDate() {
        return date;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getDocumentUid() {
        return documentUid;
    }

    public void setDocumentUid(String documentUid) {
        this.documentUid = documentUid;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getRedeemCoin() {
        return redeemCoin;
    }

    public void setRedeemCoin(Long redeemCoin) {
        this.redeemCoin = redeemCoin;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
