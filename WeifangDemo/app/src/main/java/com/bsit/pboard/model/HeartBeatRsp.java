package com.bsit.pboard.model;

public class HeartBeatRsp {

    private String binVer; // 固件版本文件名
    private String whiteVer; // 白名单版本文件名
    private String logoVer; // Logo文件名
    private String deviceCommand; //设备操作指令（复位11等）

    public HeartBeatRsp(String binVer, String whiteVer, String logoVer, String deviceCommand) {
        this.binVer = binVer;
        this.whiteVer = whiteVer;
        this.logoVer = logoVer;
        this.deviceCommand = deviceCommand;
    }

    public String getBinVer() {
        return binVer;
    }

    public void setBinVer(String binVer) {
        this.binVer = binVer;
    }

    public String getWhiteVer() {
        return whiteVer;
    }

    public void setWhiteVer(String whiteVer) {
        this.whiteVer = whiteVer;
    }

    public String getLogoVer() {
        return logoVer;
    }

    public void setLogoVer(String logoVer) {
        this.logoVer = logoVer;
    }

    public String getDeviceCommand() {
        return deviceCommand;
    }

    public void setDeviceCommand(String deviceCommand) {
        this.deviceCommand = deviceCommand;
    }
}
