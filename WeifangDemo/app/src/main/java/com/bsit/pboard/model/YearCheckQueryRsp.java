package com.bsit.pboard.model;

public class YearCheckQueryRsp {
	private String status; // 0 未激活 1 已激活
	private String validityPeriod; //有效期

	public YearCheckQueryRsp(String status, String validityPeriod) {
		this.status = status;
		this.validityPeriod = validityPeriod;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(String validityPeriod) {
		this.validityPeriod = validityPeriod;
	}
}
