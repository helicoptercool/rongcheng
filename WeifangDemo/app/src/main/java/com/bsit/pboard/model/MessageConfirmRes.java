package com.bsit.pboard.model;

/**
 * 2062圈存确认返回参数
 * 
 * @author dell
 * 
 */
public class MessageConfirmRes {

	private String respondeCode; // 响应码（09000成功，其他失败）  5位

	public String getRespondeCode() {
		return respondeCode;
	}

	public void setRespondeCode(String respondeCode) {
		this.respondeCode = respondeCode;
	}


}
