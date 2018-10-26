package com.bsit.pboard.model;

/**
 * 2061请求参数
 * 
 * @author dell
 * 
 */
public class TestRechargeInit {
	private String algInd;
	private String cardBalance;
	private String keyVer;
	private String randPBOC;
	private String sequence;
	private String mac1;
	private String amount;
	private String cardNo;
	private String termNo;
	private String transTime;

	public TestRechargeInit(String algInd, String cardBalance, String keyVer, String randPBOC, String sequence, String mac1, String amount, String cardNo, String termNo, String transTime) {
		this.algInd = algInd;
		this.cardBalance = cardBalance;
		this.keyVer = keyVer;
		this.randPBOC = randPBOC;
		this.sequence = sequence;
		this.mac1 = mac1;
		this.amount = amount;
		this.cardNo = cardNo;
		this.termNo = termNo;
		this.transTime = transTime;
	}

	public String getAlgInd() {
		return algInd;
	}

	public void setAlgInd(String algInd) {
		this.algInd = algInd;
	}

	public String getCardBalance() {
		return cardBalance;
	}

	public void setCardBalance(String cardBalance) {
		this.cardBalance = cardBalance;
	}

	public String getKeyVer() {
		return keyVer;
	}

	public void setKeyVer(String keyVer) {
		this.keyVer = keyVer;
	}

	public String getRandPBOC() {
		return randPBOC;
	}

	public void setRandPBOC(String randPBOC) {
		this.randPBOC = randPBOC;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getMac1() {
		return mac1;
	}

	public void setMac1(String mac1) {
		this.mac1 = mac1;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getTermNo() {
		return termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	@Override
	public String toString() {
		return "TestRechargeInit{" +
				"algInd='" + algInd + '\'' +
				", cardBalance='" + cardBalance + '\'' +
				", keyVer='" + keyVer + '\'' +
				", randPBOC='" + randPBOC + '\'' +
				", sequence='" + sequence + '\'' +
				", mac1='" + mac1 + '\'' +
				", amount='" + amount + '\'' +
				", cardNo='" + cardNo + '\'' +
				", termNo='" + termNo + '\'' +
				", transTime='" + transTime + '\'' +
				'}';
	}
}
