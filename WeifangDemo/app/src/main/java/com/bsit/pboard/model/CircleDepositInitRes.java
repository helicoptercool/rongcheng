package com.bsit.pboard.model;

/**
 * 圈存初始化返回对象
 */
public class CircleDepositInitRes {
    private String mOldBalance;//    电子存折或电子钱包旧余额
    private String mTransactSn;//    电子存折或电子钱包联机交易序号
    private String mSecretKeyVer;//    密钥版本号(DATA 中第一字节指定的圈存密钥的密钥版本号)
    private String mAlgorithId;//    算法标识(DATA 中第一字节指定的圈存密钥的算法标识)
    private String mPseudorandomNumber;//    伪随机数(IC 卡)
    private String mMacOne;//    MAC1

    public String getmOldBalance() {
        return mOldBalance;
    }

    public void setmOldBalance(String mOldBalance) {
        this.mOldBalance = mOldBalance;
    }

    public String getmTransactSn() {
        return mTransactSn;
    }

    public void setmTransactSn(String mTransactSn) {
        this.mTransactSn = mTransactSn;
    }

    public String getmSecretKeyVer() {
        return mSecretKeyVer;
    }

    public void setmSecretKeyVer(String mSecretKeyVer) {
        this.mSecretKeyVer = mSecretKeyVer;
    }

    public String getmAlgorithId() {
        return mAlgorithId;
    }

    public void setmAlgorithId(String mAlgorithId) {
        this.mAlgorithId = mAlgorithId;
    }

    public String getmPseudorandomNumber() {
        return mPseudorandomNumber;
    }

    public void setmPseudorandomNumber(String mPseudorandomNumber) {
        this.mPseudorandomNumber = mPseudorandomNumber;
    }

    public String getmMacOne() {
        return mMacOne;
    }

    public void setmMacOne(String mMacOne) {
        this.mMacOne = mMacOne;
    }
}
