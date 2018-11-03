package com.bsit.pboard.model;

public class YearCheckApplyRsp {
	private String apdu;
//	private String validityPeriod;


	public YearCheckApplyRsp(String apdu) {
		this.apdu = apdu;
	}

	public String getApdu() {
		return apdu;
	}

	public void setApdu(String apdu) {
		this.apdu = apdu;
	}
}
