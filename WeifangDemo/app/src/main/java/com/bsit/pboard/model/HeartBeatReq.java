package com.bsit.pboard.model;

public class HeartBeatReq {

    private String deviceId; //L29 设备唯一编号（必须代表机具的唯一性） 前5位位设备类型 例如：P209A1D002D343647093338363435
    private String supplierNo; //L8 供应商编码
    private String dateTime; //L14 心跳时间
    private String merchantNo; //L8 公司编码
    private String posId; //L12 现场环境中设备被分配的POS ID
    private String termNo; //L12 现场环境中设备被分配的终端编码
    private String samId; //L16 现场环境中设备被分配的psamId
    private String binVer; //M32 嵌入式软件版本号
    private String binDate; //L14 编译时间
    private String whiteVer; //L20 本地白名单版本号
    private String regionCode; //L4 设备所在城市编码
    private String dataCounts; //M6 设备未上传交易记录数
    private String latestTradeTime; //L14 最近一次交易时间
    private String latestBootTime; //L14 最近一次启动时间

    public HeartBeatReq(){}

    public HeartBeatReq(String deviceId, String supplierNo, String dateTime, String merchantNo, String posId, String termNo, String samId, String binVer,
                        String binDate, String whiteVer, String regionCode, String dataCounts, String latestTradeTime, String latestBootTime) {
        this.deviceId = deviceId;
        this.supplierNo = supplierNo;
        this.dateTime = dateTime;
        this.merchantNo = merchantNo;
        this.posId = posId;
        this.termNo = termNo;
        this.samId = samId;
        this.binVer = binVer;
        this.binDate = binDate;
        this.whiteVer = whiteVer;
        this.regionCode = regionCode;
        this.dataCounts = dataCounts;
        this.latestTradeTime = latestTradeTime;
        this.latestBootTime = latestBootTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getTermNo() {
        return termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public String getSamId() {
        return samId;
    }

    public void setSamId(String samId) {
        this.samId = samId;
    }

    public String getBinVer() {
        return binVer;
    }

    public void setBinVer(String binVer) {
        this.binVer = binVer;
    }

    public String getBinDate() {
        return binDate;
    }

    public void setBinDate(String binDate) {
        this.binDate = binDate;
    }

    public String getWhiteVer() {
        return whiteVer;
    }

    public void setWhiteVer(String whiteVer) {
        this.whiteVer = whiteVer;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getDataCounts() {
        return dataCounts;
    }

    public void setDataCounts(String dataCounts) {
        this.dataCounts = dataCounts;
    }

    public String getLatestTradeTime() {
        return latestTradeTime;
    }

    public void setLatestTradeTime(String latestTradeTime) {
        this.latestTradeTime = latestTradeTime;
    }

    public String getLatestBootTime() {
        return latestBootTime;
    }

    public void setLatestBootTime(String latestBootTime) {
        this.latestBootTime = latestBootTime;
    }
}
