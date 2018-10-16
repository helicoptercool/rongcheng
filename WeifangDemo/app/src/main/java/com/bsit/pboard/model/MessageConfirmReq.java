package com.bsit.pboard.model;

/**
 * 2062圈存确认请求参数
 * 
 * @author dell
 * 
 */
public class MessageConfirmReq {

	private String deviceId;			// 设备号 			12位										12位
	private String messageDateTime; 	// 时间戳			14位									14位
	private String cityCode; 			// 城市编码		4位									4位
	private String cardId; 				// 卡号			20位										20位
	private String cardSequence; 		// 卡计数器  16进制      4位
	private String reloadAmount; // 交易金额  单位：分   16进制	 8位
	private String currentBalance; // 充值后余额， 如果充值失败,则为充值前金额单位 分  16进制   8位
	private String cardMType; // 主卡类型 卡种 				 2位
	private String tac; // 写卡后卡片返回					8位						
	private String rechargeId; // 补登订单号				16位
	private String writeFlag; // 写卡结果  //00 成功,01 失败,02 未知 	2位

	public MessageConfirmReq() {
	}

	public MessageConfirmReq(String deviceId, String messageDateTime, String cityCode, String cardId, String cardSequence, String reloadAmount, String currentBalance, String cardMType, String tac, String rechargeId, String writeFlag) {
		this.deviceId = deviceId;
		this.messageDateTime = messageDateTime;
		this.cityCode = cityCode;
		this.cardId = cardId;
		this.cardSequence = cardSequence;
		this.reloadAmount = reloadAmount;
		this.currentBalance = currentBalance;
		this.cardMType = cardMType;
		this.tac = tac;
		this.rechargeId = rechargeId;
		this.writeFlag = writeFlag;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(String messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardSequence() {
		return cardSequence;
	}

	public void setCardSequence(String cardSequence) {
		this.cardSequence = cardSequence;
	}

	public String getReloadAmount() {
		return reloadAmount;
	}

	public void setReloadAmount(String reloadAmount) {
		this.reloadAmount = reloadAmount;
	}

	public String getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getCardMType() {
		return cardMType;
	}

	public void setCardMType(String cardMType) {
		this.cardMType = cardMType;
	}

	public String getTac() {
		return tac;
	}

	public void setTac(String tac) {
		this.tac = tac;
	}

	public String getRechargeId() {
		return rechargeId;
	}

	public void setRechargeId(String rechargeId) {
		this.rechargeId = rechargeId;
	}

	public String getWriteFlag() {
		return writeFlag;
	}

	public void setWriteFlag(String writeFlag) {
		this.writeFlag = writeFlag;
	}

}
