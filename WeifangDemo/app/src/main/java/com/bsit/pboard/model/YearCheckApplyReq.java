package com.bsit.pboard.model;

public class YearCheckApplyReq {
	private String termId; // 12bytes
	private String cardId; //16bytes
	private String ats;
	private String random;
	private String cardType;
	private String messageDateTime;


	public YearCheckApplyReq(String termId, String cardId, String ats, String random, String cardType, String messageDateTime) {
		this.termId = termId;
		this.cardId = cardId;
		this.ats = ats;
		this.random = random;
		this.cardType = cardType;
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

	public String getAts() {
		return ats;
	}

	public void setAts(String ats) {
		this.ats = ats;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(String messageDateTime) {
		this.messageDateTime = messageDateTime;
	}
}
