package com.bsit.pboard.model;

/**
 * 2062圈存确认请求参数
 * 
 * @author dell
 * 
 */
public class MessageConfirmReq {

	private String termId; //termId机具的蓝牙MAC地址
	private String cardId; //卡号
	private String outTradeNo; //订单号 2012中返回的
	private String cardType; //卡类型052 CPU卡，952 M1卡
	private String status; //写卡结果 00成功  01失败
	private String tac; //写卡返回tac
	private String messageDateTime;


	public MessageConfirmReq(String termId, String cardId, String outTradeNo, String cardType, String status, String tac, String messageDateTime) {
		this.termId = termId;
		this.cardId = cardId;
		this.outTradeNo = outTradeNo;
		this.cardType = cardType;
		this.status = status;
		this.tac = tac;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTac() {
		return tac;
	}

	public void setTac(String tac) {
		this.tac = tac;
	}
}
