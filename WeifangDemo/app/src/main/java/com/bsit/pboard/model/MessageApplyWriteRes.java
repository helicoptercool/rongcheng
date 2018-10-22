package com.bsit.pboard.model;

/**
 * 2061请求写卡后返回参数
 * 
 * @author dell
 * 
 */
public class MessageApplyWriteRes {

//	private String messageDateTime; // 时间戳                                    	 14位
	private String MAC2; 			// 圈存mac2                8位
	private String cardID;

	public String getMAC2() {
		return MAC2;
	}

	public void setMAC2(String MAC2) {
		this.MAC2 = MAC2;
	}

	public String getCardID() {
		return cardID;
	}

	public void setCardID(String cardID) {
		this.cardID = cardID;
	}
}
