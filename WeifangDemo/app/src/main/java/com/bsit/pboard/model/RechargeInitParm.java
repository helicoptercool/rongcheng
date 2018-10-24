package com.bsit.pboard.model;

public class RechargeInitParm {
    private String tradeType;
    private String reloadAmount;
    private String deviceId;
    private MessageQueryRes messageQueryRes;

    public RechargeInitParm(String tradeType, String reloadAmount, String deviceId, MessageQueryRes messageQueryRes) {
        this.tradeType = tradeType;
        this.reloadAmount = reloadAmount;
        this.deviceId = deviceId;
        this.messageQueryRes = messageQueryRes;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getReloadAmount() {
        return reloadAmount;
    }

    public void setReloadAmount(String reloadAmount) {
        this.reloadAmount = reloadAmount;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public MessageQueryRes getMessageQueryRes() {
        return messageQueryRes;
    }

    public void setMessageQueryRes(MessageQueryRes messageQueryRes) {
        this.messageQueryRes = messageQueryRes;
    }
}
