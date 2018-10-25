package com.bsit.pboard.model;

/**
 * 2061请求写卡后返回参数
 * 
 * @author dell
 * 
 */
public class MessageApplyWriteRes {

//	private String messageDateTime; // 时间戳                                    	 14位
	private String mac2; 			// 圈存mac2                8位
	private String cardNo;

	public String getMAC2() {
		return mac2;
	}

	public void setMAC2(String MAC2) {
		this.mac2 = MAC2;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
}
