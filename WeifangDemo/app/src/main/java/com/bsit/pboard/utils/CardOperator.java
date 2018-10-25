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

public class CardOperator {
    private static final String TAG = CardOperator.class.getName();
    private Context mContext;
    private Handler mHandler;
    private CardInfo mCardInfo;
    private int mCardType;
    private String cpuCardType = "052";

    public CardOperator(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void signIn() {
        HttpBusiness.signIn(mHandler);
    }

    public void findCard() {
        int cardTransport = 200;//交通部
        try {
            String cardSn = CardBusiness.findCard();
            if (!TextUtils.isEmpty(cardSn)) {
                if (mCardType == cardTransport) {
                    mCardInfo = CardBusiness.readOtherCard();
                } else {
                    mCardInfo = CardBusiness.getCardInfo();
                }
                Log.i(TAG, "card info = " + mCardInfo.toString());

//                        String data0015 = cardBusiness.getData0015();
//                        Log.i(TAG, "data0015 === " + data0015);


                HttpBusiness.queryOrder(new MessageQueryReq(MacUtils.getMac(), HttpBusiness.getTime(), mCardInfo.getCardNo(), mCardInfo.getBalance()), mHandler);
//                        cardBusiness.getTopInitInfo(cardInfo, "8888", deviceId);
                if (mCardInfo.getIsUse()) {
                    Message message = mHandler.obtainMessage();
                    message.what = ConstantMsg.SHOW_CARD_MSG_WHAT;
                    message.obj = mCardInfo;
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(ConstantMsg.NOT_USE_MSG_WHAT);
                }
            }
        } catch (final CardBusiness.FindCardException e) {
            mCardInfo = null;
            mHandler.sendEmptyMessageDelayed(ConstantMsg.FIND_CARD_MSG_WHAT, 1000);
        } catch (final CardBusiness.ReadCardException e) {
            mCardInfo = null;
            if (e.getMessage().equals(mContext.getString(R.string.str_get_main_dir_failure))) {
                mHandler.sendEmptyMessage(ConstantMsg.UNRECOGNIZE_CARD);
            }
            if (mCardType == cardTransport) {
                mCardType = 100;//住建部
            } else {
                mCardType = cardTransport;
            }
            mHandler.sendEmptyMessageDelayed(ConstantMsg.FIND_CARD_MSG_WHAT, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rechargeInit(String tradeType,String money, String deviceID,String outTradeNo) {
        try {
            mCardInfo = CardBusiness.getTopInitInfo(mCardInfo, money, deviceID);
//            MessageQueryRes messageQueryRes = mRechargeInitParm.getMessageQueryRes();
            if (tradeType.equals("01")) { //01 IC卡充值
                HttpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), mCardInfo.getCardNo(), cpuCardType, tradeType,
                        outTradeNo, mCardInfo.getCardRand(), mCardInfo.getCardTradeNo(), mCardInfo.getBalance(), money,
                        mCardInfo.getQcMac(), "", "", HttpBusiness.getTime()), mHandler);
            } else if (tradeType.equals("02")) { //02月票充值
                String data0015 = CardBusiness.getData0015();
                String ats = "";
                if (data0015.length() > 16) {
                    ats = data0015.substring(data0015.length() - 8, data0015.length());
                }
                HttpBusiness.monthTicketModify(new MonthTicketModifyReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, cpuCardType, data0015, ats, mCardInfo.getCardRand()), mHandler);
//                        httpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), cardInfo.getCardNo(), cardType, messageQueryRes.getTradetype(),
//                                messageQueryRes.getOutTradeNo(), cardInfo.getCardRand(), cardInfo.getCardSeq(), cardInfo.getBalance(), messageQueryRes.getMoney(), cardInfo.getQcMac(), "", "", HttpBusiness.getTime()), handler);
            }
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.SHOW_FAILDRECHARGE_MSG_WHAT);
        }
    }

    public void rechargeConfirm(String outTradeNo, String writeCardStatus) {
        try {
            HttpBusiness.confirmRecharge(new MessageConfirmReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, cpuCardType, writeCardStatus, mCardInfo.getTac(),HttpBusiness.getDealStamp()), mHandler);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.SHOW_FAILDRECHARGE_MSG_WHAT);
        }
    }

    public void setCardInfo(CardInfo info) {
        mCardInfo = info;
    }
}
