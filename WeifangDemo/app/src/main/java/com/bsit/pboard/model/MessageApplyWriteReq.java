package com.bsit.pboard.model;

/**
 * 2061请求参数
 * 
 * @author dell
 * 
 */
public class MessageApplyWriteReq {

	private String deviceId;// 设备号 要求12位		12位
	private String cityCode; // 城市编码			4位
	private String cardId; // 卡号				20位
	private String cardMType; // 主卡类型			2位
	private String cardSType; // 子卡类型			2位
	private String csn; // 卡芯片号				8位
	private String deposit; // 押金		16进制        8位
	private String reloadAmount; //圈存金额16进制      8位
	private String srcBal; // 卡片原额		16进制        8位
	private String cardSequence; // 卡计数器16进制   4位
	private String keyVer; // 卡片密钥版本                       2位
	private String alglnd; // 卡片算法标识 			2位
	private String cardRand; // 卡片随机数,16进制        8位
	private String mac1; // 卡片初始化返回的mac1     8位
	private String rechargeId; // 补登订单号		16位

	public MessageApplyWriteReq() {
	}

	public MessageApplyWriteReq(String deviceId, String cityCode, String cardId, String cardMType, String cardSType,
								String csn, String deposit, String reloadAmount, String srcBal, String cardSequence, String keyVer,
								String alglnd, String cardRand, String mac1, String rechargeId) {
		this.deviceId = deviceId;
		this.cityCode = cityCode;
		this.cardId = cardId;
		this.cardMType = cardMType;
		this.cardSType = cardSType;
		this.csn = csn;
		this.deposit = deposit;
		this.reloadAmount = reloadAmount;
		this.srcBal = srcBal;
		this.cardSequence = cardSequence;
		this.keyVer = keyVer;
		this.alglnd = alglnd;
		this.cardRand = cardRand;
		this.mac1 = mac1;
		this.rechargeId = rechargeId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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

	public String getCardMType() {
		return cardMType;
	}

	public void setCardMType(String cardMType) {
		this.cardMType = cardMType;
	}

	public String getCardSType() {
		return cardSType;
	}

	public void setCardSType(String cardSType) {
		this.cardSType = cardSType;
	}

	public String getCsn() {
		return csn;
	}

	public void setCsn(String csn) {
		this.csn = csn;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getReloadAmount() {
		return reloadAmount;
	}

	public void setReloadAmount(String reloadAmount) {
		this.reloadAmount = reloadAmount;
	}

	public String getSrcBal() {
		return srcBal;
	}

	public void setSrcBal(String srcBal) {
		this.srcBal = srcBal;
	}

	public String getCardSequence() {
		return cardSequence;
	}

	public void setCardSequence(String cardSequence) {
		this.cardSequence = cardSequence;
	}

	public String getKeyVer() {
		return keyVer;
	}

	public void setKeyVer(String keyVer) {
		this.keyVer = keyVer;
	}

	public String getAlglnd() {
		return alglnd;
	}

	public void setAlglnd(String alglnd) {
		this.alglnd = alglnd;
	}

	public String getCardRand() {
		return cardRand;
	}

	public void setCardRand(String cardRand) {
		this.cardRand = cardRand;
	}

	public String getMac1() {
		return mac1;
	}

	public void setMac1(String mac1) {
		this.mac1 = mac1;
	}

	public String getRechargeId() {
		return rechargeId;
	}

	public void setRechargeId(String rechargeId) {
		this.rechargeId = rechargeId;
	}

}
