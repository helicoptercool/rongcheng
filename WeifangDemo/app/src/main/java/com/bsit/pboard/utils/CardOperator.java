package com.bsit.pboard.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bsit.pboard.R;
import com.bsit.pboard.business.CardBusiness;
import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.MessageApplyWriteReq;
import com.bsit.pboard.model.MessageConfirmReq;
import com.bsit.pboard.model.MessageQueryReq;
import com.bsit.pboard.model.MonthTicketModifyReq;
import com.bsit.pboard.model.TestRechargeInit;

import static com.bsit.pboard.model.Constants.DEVICE_ID;

public class CardOperator {
    private static final String TAG = CardOperator.class.getName();
    private Context mContext;
    private Handler mHandler;
    private CardInfo mCardInfo;
    private String cpuCardType = "052";
    private CardBusiness mCardBusiness;
    private String mAts = "";
    private int mCardType;

    public CardOperator(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        mCardBusiness = CardBusiness.getInstance(mContext);
    }

    public void signIn() {
        HttpBusiness.signIn(mHandler);
    }

    public void findCard(boolean isRecharge) {
        int cardTransport = 200;//交通部
        try {
            String cardSn = mCardBusiness.findCard();
            if (!TextUtils.isEmpty(cardSn)) {
                //0004D5B4816F8F28147880B0022090464D434F209165CC3BD5B4816F9000
                int len = cardSn.length();
                if (len >= 20) {
                    mAts = cardSn.substring(len - 4 - 16, len - 4);
                }
                if (mCardType == cardTransport) {
                    mCardInfo = mCardBusiness.readOtherCard();
                } else {
                    mCardInfo = mCardBusiness.getCardInfo();
                }
//                if (isRecharge) {
//                    Log.i(TAG, "has recharge, continue find cand , but not recharg...");
//                    mHandler.sendEmptyMessage(ConstantMsg.MSG_HAS_RECHERGED);
//                    return;
//                }
                Log.i(TAG, "card info = " + mCardInfo.toString());
                HttpBusiness.queryOrder(new MessageQueryReq(MacUtils.getMac(), HttpBusiness.getTime(), mCardInfo.getCardNo(), mCardInfo.getBalance()), mHandler);
                if (mCardInfo.getIsUse()) {
                    Message message = mHandler.obtainMessage();
                    message.what = ConstantMsg.VIEW_SHOW_CARD;
                    message.obj = mCardInfo;
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(ConstantMsg.VIEW_CARD_NOT_USE);
                }
            }
        } catch (final CardBusiness.FindCardException e) {
            mCardInfo = null;
            mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_FIND_CARD, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
        } catch (final CardBusiness.ReadCardException e) {
            mCardInfo = null;
            if (e.getMessage().equals(mContext.getString(R.string.str_get_main_dir_failure))) {
                mHandler.sendEmptyMessage(ConstantMsg.VIEW_UNRECOGNIZE_CARD);
                return;
            }
            if (mCardType == cardTransport) {
                mCardType = 100;//住建部
            } else {
                mCardType = cardTransport;
            }
            mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_FIND_CARD, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rechargeInit(String tradeType, String money, String deviceID, String outTradeNo) {
        try {
            mCardInfo = mCardBusiness.getTopInitInfo(mCardInfo, money, deviceID);
            String data0015 = "";
            if (tradeType.equals("02")){
                data0015 = mCardBusiness.getData0015();
            }
            HttpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), mCardInfo.getCardNo(), cpuCardType, tradeType,
                    outTradeNo, mCardInfo.getCardRand(), mCardInfo.getCardTradeNo(), mCardInfo.getBalance(), money,
                    mCardInfo.getQcMac(), data0015, "", HttpBusiness.getDealStamp()), mHandler);
//            if (tradeType.equals("01")) { //01 IC卡充值
//                HttpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), mCardInfo.getCardNo(), cpuCardType, tradeType,
//                        outTradeNo, mCardInfo.getCardRand(), mCardInfo.getCardTradeNo(), mCardInfo.getBalance(), money,
//                        mCardInfo.getQcMac(), "", "", HttpBusiness.getDealStamp()), mHandler);
//            } else if (tradeType.equals("02")) { //02月票充值
//                String data0015 = mCardBusiness.getData0015();
//                String ats = mCardInfo.getCardRand()+mCardInfo.getQcMac();

//                String ats = "";
//                if (data0015.length() > 16) {
//                    ats = data0015.substring(data0015.length() - 16, data0015.length());
//                }

//                HttpBusiness.monthTicketModify(new MonthTicketModifyReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, cpuCardType, data0015, mAts, mCardInfo.getCardRand(), HttpBusiness.getDealStamp()), mHandler);

//            }
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.MSG_FAIL_RECHARGE);
        }
    }

    public void monthTicketModify(String outTradeNo) {
        String data0015 = mCardBusiness.getData0015();
        HttpBusiness.monthTicketModify(new MonthTicketModifyReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, cpuCardType, data0015, mAts, mCardInfo.getCardRand(), HttpBusiness.getDealStamp()), mHandler);
    }

    public void rechargeConfirm(String outTradeNo, String writeCardStatus) {
        try {
            HttpBusiness.confirmRecharge(new MessageConfirmReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, cpuCardType, writeCardStatus, mCardInfo.getTac(), HttpBusiness.getDealStamp()), mHandler);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.MSG_FAIL_RECHARGE_CONFIRM);
        }
    }


    public CardInfo testFindCard() {
        int cardTransport = 200;//交通部
        try {
            String cardSn = mCardBusiness.findCard();
            if (!TextUtils.isEmpty(cardSn)) {
                mCardInfo = mCardBusiness.testReadOtherCard();
                Log.i(TAG, "card info = " + mCardInfo.toString());
            }
            mHandler.sendEmptyMessage(555);
            return mCardInfo;
        } catch (final CardBusiness.FindCardException e) {
            mCardInfo = null;
            mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_FIND_CARD, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
        } catch (final CardBusiness.ReadCardException e) {
            mCardInfo = null;
            if (e.getMessage().equals(mContext.getString(R.string.str_get_main_dir_failure))) {
                mHandler.sendEmptyMessage(ConstantMsg.VIEW_UNRECOGNIZE_CARD);
                return null;
            }
            if (mCardType == cardTransport) {
                mCardType = 100;//住建部
            } else {
                mCardType = cardTransport;
            }
            mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_FIND_CARD, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void testRechargeInit(String money, String deviceID, String outTradeNo) {

        try {
            mCardInfo = mCardBusiness.testGetTopInitInfo(mCardInfo, "100", deviceID);
            Log.i(TAG, "testRechargeInit");
            HttpBusiness.testRechargeCard(new TestRechargeInit(mCardInfo.getAlglnd(), mCardInfo.getBalance(), mCardInfo.getKeyVer(), mCardInfo.getCardRand(),
                    mCardInfo.getCardTradeNo(), mCardInfo.getQcMac(), "100", mCardInfo.getCardNo(), DEVICE_ID, HttpBusiness.getDealStamp()), mHandler);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.MSG_FAIL_RECHARGE);
        }
    }

    public void setCardInfo(CardInfo info) {
        mCardInfo = info;
    }
}
