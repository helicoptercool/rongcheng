package com.bsit.pboard.model;

/**
 * 2012请求参数
 * @author dell
 *
 */
public class MessageQueryReq {

	private String deviceId; // 设备号                      12位
	private String cardId; // 卡号                               20位
	private String cityCode; // 城市号                       4位
	private String csn; //卡芯片号                                 8位
	private String cardSType; // 子卡类型    传卡种类别：交通部卡 2市民卡 3…   2位
	private String cardMType; // 主卡类型                   2位
	private String srcBal; // 最新余额（16进制数据，8位）

	public MessageQueryReq() {
	}

	public MessageQueryReq(String deviceId, String cardId, String cityCode, String csn, String cardSType, String cardMType, String srcBal) {
		this.deviceId = deviceId;
		this.cardId = cardId;
		this.cityCode = cityCode;
		this.csn = csn;
		this.cardSType = cardSType;
		this.cardMType = cardMType;
		this.srcBal = srcBal;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCsn() {
		return csn;
	}

	public void setCsn(String csn) {
		this.csn = csn;
	}

	public String getCardSType() {
		return cardSType;
	}

	public void setCardSType(String cardSType) {
		this.cardSType = cardSType;
	}

	public String getCardMType() {
		return cardMType;
	}

	public void setCardMType(String cardMType) {
		this.cardMType = cardMType;
	}

	public String getSrcBal() {
		return srcBal;
	}

	public void setSrcBal(String srcBal) {
		this.srcBal = srcBal;
	}

}
