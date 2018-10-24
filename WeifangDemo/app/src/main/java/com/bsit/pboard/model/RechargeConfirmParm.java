package com.bsit.pboard.model;

public class RechargeConfirmParm {
    private String outTradeNo;
    private String writeCardStatus;

    public RechargeConfirmParm(String outTradeNo, String writeCardStatus) {
        this.outTradeNo = outTradeNo;
        this.writeCardStatus = writeCardStatus;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getWriteCardStatus() {
        return writeCardStatus;
    }

    public void setWriteCardStatus(String writeCardStatus) {
        this.writeCardStatus = writeCardStatus;
    }
}
