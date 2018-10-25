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
    private CardBusiness mCardBusiness;

    public CardOperator(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        mCardBusiness = CardBusiness.getInstance(mContext);
    }

    public void signIn() {
        HttpBusiness.signIn(mHandler);
    }

    public void findCard() {
        int cardTransport = 200;//交通部
        try {
            String cardSn = mCardBusiness.findCard();
            if (!TextUtils.isEmpty(cardSn)) {
                if (mCardType == cardTransport) {
                    mCardInfo = mCardBusiness.readOtherCard();
                } else {
                    mCardInfo = mCardBusiness.getCardInfo();
                }
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
            mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_FIND_CARD, ConstantMsg.TIME_INTEVAL);
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
            mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_FIND_CARD, ConstantMsg.TIME_INTEVAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rechargeInit(String tradeType,String money, String deviceID,String outTradeNo) {
        try {
            mCardInfo = mCardBusiness.getTopInitInfo(mCardInfo, money, deviceID);
//            MessageQueryRes messageQueryRes = mRechargeInitParm.getMessageQueryRes();
            if (tradeType.equals("01")) { //01 IC卡充值
                HttpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), mCardInfo.getCardNo(), cpuCardType, tradeType,
                        outTradeNo, mCardInfo.getCardRand(), mCardInfo.getCardTradeNo(), mCardInfo.getBalance(), money,
                        mCardInfo.getQcMac(), "", "", HttpBusiness.getDealStamp()), mHandler);
            } else if (tradeType.equals("02")) { //02月票充值
                String data0015 = mCardBusiness.getData0015();
                String ats = "";
                if (data0015.length() > 16) {
                    ats = data0015.substring(data0015.length() - 8, data0015.length());
                }
                HttpBusiness.monthTicketModify(new MonthTicketModifyReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, cpuCardType, data0015, ats, mCardInfo.getCardRand(),HttpBusiness.getDealStamp()), mHandler);
//                        httpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), cardInfo.getCardNo(), cardType, messageQueryRes.getTradetype(),
//                                messageQueryRes.getOutTradeNo(), cardInfo.getCardRand(), cardInfo.getCardSeq(), cardInfo.getBalance(), messageQueryRes.getMoney(), cardInfo.getQcMac(), "", "", HttpBusiness.getTime()), handler);
            }
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.MSG_FAIL_RECHARGE);
        }
    }

    public void rechargeConfirm(String outTradeNo, String writeCardStatus) {
        try {
            HttpBusiness.confirmRecharge(new MessageConfirmReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, cpuCardType, writeCardStatus, mCardInfo.getTac(),HttpBusiness.getDealStamp()), mHandler);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.MSG_FAIL_RECHARGE_CONFIRM);
        }
    }

    public void setCardInfo(CardInfo info) {
        mCardInfo = info;
    }
}
