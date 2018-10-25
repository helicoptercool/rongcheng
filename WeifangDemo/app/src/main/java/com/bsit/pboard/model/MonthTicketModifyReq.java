package com.bsit.pboard.model;


public class MonthTicketModifyReq {

	private String termId;
	private String cardId;
	private String outTradeNo;
	private String cardType;
	private String data0015;
	private String ats;
	private String termTradeNo;
	private String messageDateTime;

	public MonthTicketModifyReq(String termId, String cardId, String outTradeNo, String cardType, String data0015, String ats, String termTradeNo, String messageDateTime) {
		this.termId = termId;
		this.cardId = cardId;
		this.outTradeNo = outTradeNo;
		this.cardType = cardType;
		this.data0015 = data0015;
		this.ats = ats;
		this.termTradeNo = termTradeNo;
		this.messageDateTime = messageDateTime;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getData0015() {
		return data0015;
	}

	public void setData0015(String data0015) {
		this.data0015 = data0015;
	}

	public String getAts() {
		return ats;
	}

	public void setAts(String ats) {
		this.ats = ats;
	}

	public String getTermTradeNo() {
		return termTradeNo;
	}

	public void setTermTradeNo(String termTradeNo) {
		this.termTradeNo = termTradeNo;
	}

	public String getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(String messageDateTime) {
		this.messageDateTime = messageDateTime;
	}
}
