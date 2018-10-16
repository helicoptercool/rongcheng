package com.bsit.pboard.model;

import java.io.Serializable;

/**
 * Created by shengbing on 2016/7/25.
 */
public class CardInfo implements Serializable {

    private String cardIssuerLogo; //发卡机构标识
    private String appTypeIdentification; //应用类型标识
    private String appVersionOrganization; //发卡机构应用版本
    private String cardNo; //卡号
    private String startTime; //开卡时间
    private String endTime;   //卡失效时间
    private String fciData;   //发卡机构自定义FCI数据
    private String balance;   //余额
    private String cardSeq;   //计数器
    private String keyVer;    //密钥版本
    private String alglnd;    //算法标识
    private String cardRand;  //随机数
    private String qcMac;
    private String messageDateTime;
    private String cardSType;  //主卡类型
    private String cardMType;  //子卡类型
    private String tac;

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getCardIssuerLogo() {
        return cardIssuerLogo;
    }

    public void setCardIssuerLogo(String cardIssuerLogo) {
        this.cardIssuerLogo = cardIssuerLogo;
    }

    public String getAppTypeIdentification() {
        return appTypeIdentification;
    }

    public void setAppTypeIdentification(String appTypeIdentification) {
        this.appTypeIdentification = appTypeIdentification;
    }

    public String getAppVersionOrganization() {
        return appVersionOrganization;
    }

    public void setAppVersionOrganization(String appVersionOrganization) {
        this.appVersionOrganization = appVersionOrganization;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFciData() {
        return fciData;
    }

    public void setFciData(String fciData) {
        this.fciData = fciData;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCardSeq() {
        return cardSeq;
    }

    public void setCardSeq(String cardSeq) {
        this.cardSeq = cardSeq;
    }

    public String getKeyVer() {
        return keyVer;
    }

    public void setKeyVer(String keyVer) {
        this.keyVer = keyVer;
    }

    public String getAlglnd() {
        return alglnd;
    }

    public void setAlglnd(String alglnd) {
        this.alglnd = alglnd;
    }

    public String getCardRand() {
        return cardRand;
    }

    public void setCardRand(String cardRand) {
        this.cardRand = cardRand;
    }

    public String getQcMac() {
        return qcMac;
    }

    public void setQcMac(String qcMac) {
        this.qcMac = qcMac;
    }

    public String getMessageDateTime() {
        return messageDateTime;
    }

    public void setMessageDateTime(String messageDateTime) {
        this.messageDateTime = messageDateTime;
    }

    public String getCardSType() {
        return cardSType;
    }

    public void setCardSType(String cardSType) {
        this.cardSType = cardSType;
    }

    public String getCardMType() {
        return cardMType;
    }

    public void setCardMType(String cardMType) {
        this.cardMType = cardMType;
    }
}
