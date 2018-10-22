package com.bsit.pboard.model;

/**
 * 2012返回参数
 *
 * @author dell
 */
public class MessageQueryRes {

//    private String deviceId; // 机具编号                                                 12位
//    private String reloadAmount; // 订单圈存金额 ,     16进制       8位
//    private String rechargeId; // 补登流水号                                           16位
//    private String respondeCode; // 响应码（09000成功，其他失败）  5位


    private String tradetype; //01 IC卡充值,02月票充值
    private String money; //充值金额
    private String startdate; //本次充值起始日期
    private String enddate; //本次充值结束日期
    private String datedif; //月数（起始日期和结束日期的差）
    private String outTradeNo; //代补登订单号

    public String getTradetype() {
        return tradetype;
    }

    public void setTradetype(String tradetype) {
        this.tradetype = tradetype;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getDatedif() {
        return datedif;
    }

    public void setDatedif(String datedif) {
        this.datedif = datedif;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
}
