package com.bsit.pboard.model;

/**
 * 2061请求写卡后返回参数
 * 
 * @author dell
 * 
 */
public class MonthTicketModifyRes {

	private String cardNo;
	private String apdu;

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getApdu() {
		return apdu;
	}

	public void setApdu(String apdu) {
		this.apdu = apdu;
	}
}
