package com.bsit.pboard.model;

public class YearCheckQueryReq {
	private String termId; // 12bytes
	private String cardId; //16bytes

	public YearCheckQueryReq(String termId, String cardId) {
		this.termId = termId;
		this.cardId = cardId;
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
}
