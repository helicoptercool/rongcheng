package com.bsit.pboard.model;

/**
 * 2012请求参数
 * @author dell
 *
 */
public class MessageQueryReq {

//	private String deviceId; // 设备号                      12位
//	private String cityCode; // 城市号                       4位
//	private String csn; //卡芯片号                                 8位
//	private String cardSType; // 子卡类型    传卡种类别：交通部卡 2市民卡 3…   2位
//	private String cardMType; // 主卡类型                   2位

	private String termId; //机具的蓝牙MAC地址
	private String messageDateTime;
	private String cardId; // 卡号                               20位
	private String srcBal; // 最新余额（16进制数据，8位）

	public MessageQueryReq(String termId, String messageDateTime, String cardId, String srcBal) {
		this.termId = termId;
		this.messageDateTime = messageDateTime;
		this.cardId = cardId;
		this.srcBal = srcBal;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(String messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getSrcBal() {
		return srcBal;
	}

	public void setSrcBal(String srcBal) {
		this.srcBal = srcBal;
	}
}
