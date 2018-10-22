package com.bsit.pboard.model;

/**
 * 2062圈存确认请求参数
 * 
 * @author dell
 * 
 */
public class MessageConfirmReq {

//	private String deviceId;			// 设备号 			12位										12位
//	private String messageDateTime; 	// 时间戳			14位									14位
//	private String cityCode; 			// 城市编码		4位									4位
//	private String cardId; 				// 卡号			20位										20位
//	private String cardSequence; 		// 卡计数器  16进制      4位
//	private String reloadAmount; // 交易金额  单位：分   16进制	 8位
//	private String currentBalance; // 充值后余额， 如果充值失败,则为充值前金额单位 分  16进制   8位
//	private String cardMType; // 主卡类型 卡种 				 2位
//	private String tac; // 写卡后卡片返回					8位
//	private String rechargeId; // 补登订单号				16位
//	private String writeFlag; // 写卡结果  //00 成功,01 失败,02 未知 	2位

	private String termId; //termId机具的蓝牙MAC地址
	private String cardId; //卡号
	private String outTradeNo; //订单号 2012中返回的
	private String cardType; //卡类型052 CPU卡，952 M1卡
	private String status; //写卡结果 00成功  01失败
	private String tac; //写卡返回tac


	public MessageConfirmReq(String termId, String cardId, String outTradeNo, String cardType, String status, String tac) {
		this.termId = termId;
		this.cardId = cardId;
		this.outTradeNo = outTradeNo;
		this.cardType = cardType;
		this.status = status;
		this.tac = tac;
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
