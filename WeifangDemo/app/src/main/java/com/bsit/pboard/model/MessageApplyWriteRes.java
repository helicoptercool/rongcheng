package com.bsit.pboard.model;

/**
 * 2061请求写卡后返回参数
 * 
 * @author dell
 * 
 */
public class MessageApplyWriteRes {

	private String messageDateTime; // 时间戳                                    	 14位
	private String mac2; 			// 圈存mac2                8位
	private String respondeCode; 	// 响应码（09000成功，其他失败）  5位

	public String getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(String messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	public String getMac2() {
		return mac2;
	}

	public void setMac2(String mac2) {
		this.mac2 = mac2;
	}

	public String getRespondeCode() {
		return respondeCode;
	}

	public void setRespondeCode(String respondeCode) {
		this.respondeCode = respondeCode;
	}

}
