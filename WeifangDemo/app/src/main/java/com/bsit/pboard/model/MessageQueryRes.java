package com.bsit.pboard.model;

/**
 * 2012返回参数
 * @author dell
 *
 */
public class MessageQueryRes {

	private String deviceId; // 机具编号                                                 12位
	private String reloadAmount; // 订单圈存金额 ,     16进制       8位
	private String rechargeId; // 补登流水号                                           16位
	private String respondeCode; // 响应码（09000成功，其他失败）  5位

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getReloadAmount() {
		return reloadAmount;
	}

	public void setReloadAmount(String reloadAmount) {
		this.reloadAmount = reloadAmount;
	}

	public String getRechargeId() {
		return rechargeId;
	}

	public void setRechargeId(String rechargeId) {
		this.rechargeId = rechargeId;
	}

	public String getRespondeCode() {
		return respondeCode;
	}

	public void setRespondeCode(String respondeCode) {
		this.respondeCode = respondeCode;
	}

}
