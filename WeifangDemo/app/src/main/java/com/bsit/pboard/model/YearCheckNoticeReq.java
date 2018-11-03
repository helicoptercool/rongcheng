package com.bsit.pboard.model;

public class YearCheckNoticeReq {
	private String termId; // 12bytes
	private String cardId; //16bytes
	private String writecCardResult; //年检写卡结果  SUCCESS/FAIL
	private String messageDateTime;

	public YearCheckNoticeReq(String termId, String cardId, String writecCardResult, String messageDateTime) {
		this.termId = termId;
		this.cardId = cardId;
		this.writecCardResult = writecCardResult;
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

	public String getWritecCardResult() {
		return writecCardResult;
	}

	public void setWritecCardResult(String writecCardResult) {
		this.writecCardResult = writecCardResult;
	}

	public String getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(String messageDateTime) {
		this.messageDateTime = messageDateTime;
	}
}
