package com.bsit.pboard.model;

/**
 * 2061请求参数
 * 
 * @author dell
 * 
 */
public class MessageApplyWriteReq {

//	private String deviceId;// 设备号 要求12位		12位
//	private String cityCode; // 城市编码			4位
//	private String cardId; // 卡号				20位
//	private String cardMType; // 主卡类型			2位
//	private String cardSType; // 子卡类型			2位
//	private String csn; // 卡芯片号				8位
//	private String deposit; // 押金		16进制        8位
//	private String reloadAmount; //圈存金额16进制      8位
//	private String srcBal; // 卡片原额		16进制        8位
//	private String cardSequence; // 卡计数器16进制   4位
//	private String keyVer; // 卡片密钥版本                       2位
//	private String alglnd; // 卡片算法标识 			2位
//	private String cardRand; // 卡片随机数,16进制        8位
//	private String mac1; // 卡片初始化返回的mac1     8位
//	private String rechargeId; // 补登订单号		16位


	private String termId; //termId机具的蓝牙MAC地址
	private String cardId ; //卡号
	private String cardType; //卡类型052 CPU卡，952 M1卡
	private String tradetype; //交易类型 01 IC卡充值  02月票充值
	private String outTradeNo; //订单号 2012中返回的
	private String rndnumber ; //4字节伪随机数圈存准备产生
	private String cardTradeNo; //卡交易序号
	private String cardBalance; //电子钱包余额单位为分16进制
	private String tradeMoney ; //交易金额单位为分10进制月票充值为（次数*十进制月基）
	private String mac1 ; //Mac1
	private String data0015; //交易类型为月票充值时才需再传
	private String base; //交易类型为月票充值时才需上送
	private String messageDateTime; //前置服务器时间（机具圈存初始化时间，2062也用这个时间）


	public 	MessageApplyWriteReq(String termId, String cardId, String cardType, String tradetype, String outTradeNo, String rndnumber, String cardTradeNo, String cardBalance, String tradeMoney, String mac1, String data0015, String base, String messageDateTime) {
		this.termId = termId;
		this.cardId = cardId;
		this.cardType = cardType;
		this.tradetype = tradetype;
		this.outTradeNo = outTradeNo;
		this.rndnumber = rndnumber;
		this.cardTradeNo = cardTradeNo;
		this.cardBalance = cardBalance;
		this.tradeMoney = tradeMoney;
		this.mac1 = mac1;
		this.data0015 = data0015;
		this.base = base;
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

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getTradetype() {
		return tradetype;
	}

	public void setTradetype(String tradetype) {
		this.tradetype = tradetype;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getRndnumber() {
		return rndnumber;
	}

	public void setRndnumber(String rndnumber) {
		this.rndnumber = rndnumber;
	}

	public String getCardTradeNo() {
		return cardTradeNo;
	}

	public void setCardTradeNo(String cardTradeNo) {
		this.cardTradeNo = cardTradeNo;
	}

	public String getCardBalance() {
		return cardBalance;
	}

	public void setCardBalance(String cardBalance) {
		this.cardBalance = cardBalance;
	}

	public String getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(String tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	public String getMac1() {
		return mac1;
	}

	public void setMac1(String mac1) {
		this.mac1 = mac1;
	}

	public String getData0015() {
		return data0015;
	}

	public void setData0015(String data0015) {
		this.data0015 = data0015;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(String messageDateTime) {
		this.messageDateTime = messageDateTime;
	}
}
