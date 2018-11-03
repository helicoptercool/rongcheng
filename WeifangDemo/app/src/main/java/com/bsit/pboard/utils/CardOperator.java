package com.bsit.pboard.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bsit.pboard.R;
import com.bsit.pboard.business.CardBusiness;
import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.constant.ConstantMsg;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.MessageApplyWriteReq;
import com.bsit.pboard.model.MessageConfirmReq;
import com.bsit.pboard.model.MessageQueryReq;
import com.bsit.pboard.model.MonthTicketModifyReq;
import com.bsit.pboard.model.YearCheckApplyReq;
import com.bsit.pboard.model.YearCheckNoticeReq;
import com.bsit.pboard.model.YearCheckQueryReq;

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

    public void findCard(final String lastCardNo) {

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "card type = " + mCardType);
                try {
                    String cardSn = mCardBusiness.findCard();
                    if (!TextUtils.isEmpty(cardSn)) {
                        //0004D5B4816F8F28147880B0022090464D434F209165CC3BD5B4816F9000
                        int len = cardSn.length();
                        if (len >= 20) {
                            mAts = cardSn.substring(len - 4 - 16, len - 4);
                        }
                        if (mCardType == ConstantMsg.CARD_TYPE_TRANSPORT) {
                            mCardInfo = mCardBusiness.readOtherCard();
                        } else {
                            mCardInfo = mCardBusiness.getCardInfo();
                        }
                        String cardNo = mCardInfo.getCardNo();
                        Log.i(TAG, "card info = " + mCardInfo.toString());
                        if (lastCardNo != null && lastCardNo.equals(cardNo)) {
                            Log.i(TAG, "has recharge, continue find cand , but not recharg...");
                            mHandler.sendEmptyMessage(ConstantMsg.MSG_HAS_RECHERGED);
                            return;
                        } else {
                            mHandler.sendEmptyMessage(ConstantMsg.MSG_CARD_CHANGE_TYPE);
                        }
                        if (mCardInfo.getIsUse()) {
                            Message message = mHandler.obtainMessage();
                            message.what = ConstantMsg.VIEW_SHOW_CARD;
                            message.obj = mCardInfo;
                            mHandler.sendMessage(message);
                        } else {
                            mHandler.sendEmptyMessage(ConstantMsg.VIEW_CARD_NOT_USE);
                        }
                        if (mCardInfo.getCardSType().equals(ConstantMsg.CARD_TYPE_OLD)) {//老年卡进行年检
                            HttpBusiness.queryYearCheck(new YearCheckQueryReq(MacUtils.getMac(), mCardInfo.getCardNo()), mHandler);
                        } else {//非老年卡查询订单
                            HttpBusiness.queryOrder(new MessageQueryReq(MacUtils.getMac(), HttpBusiness.getTime(), mCardInfo.getCardNo(), mCardInfo.getBalance()), mHandler);
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
                    changeCardType();
                } catch (Exception e) {
                    mCardInfo = null;
                    changeCardType();
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void changeCardType() {
        if (mCardType == ConstantMsg.CARD_TYPE_TRANSPORT) {
            mCardType = ConstantMsg.CARD_TYPE_HOUSE;
        } else {
            mCardType = ConstantMsg.CARD_TYPE_TRANSPORT;
        }
        mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_FIND_CARD, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
    }

    public void rechargeInit(String tradeType, String money, String deviceID, String outTradeNo) {
        try {
            mCardInfo = mCardBusiness.getTopInitInfo(mCardInfo, money, deviceID, mCardType);
            String data0015 = "";
            if (tradeType.equals("02")) {
//                data0015 = mCardBusiness.getData0015();
                data0015 = mCardBusiness.getData0015New();
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

    public void sendMonthPdu(String[] pdus, boolean isOriginal, String outTradeNo) {
        String writeResponse = mCardBusiness.sendPdus(pdus, isOriginal);
        // TODO: 18-10-27 writeStatus 00, 01
        String writeStatus = writeResponse.substring(0, 0);
        rechargeConfirm(outTradeNo, writeStatus);
    }

    public void sendPdus(String[] pdus){
        String writeResponse = mCardBusiness.sendPdus(pdus,false);
        // TODO: 18-11-1 get the write result 
        String isWriteOk = writeResponse.substring(0,0);
        //年检写卡结果  SUCCESS/FAIL
        HttpBusiness.noticeYearCheck(new YearCheckNoticeReq(MacUtils.getMac(),mCardInfo.getCardNo(),"SUCCESS",HttpBusiness.getDealStamp()),mHandler);
    }

    public void rechargeConfirm(String outTradeNo, String writeCardStatus) {
        try {
            HttpBusiness.confirmRecharge(new MessageConfirmReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, cpuCardType, writeCardStatus, mCardInfo.getTac(), HttpBusiness.getDealStamp()), mHandler);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.MSG_FAIL_RECHARGE_CONFIRM);
        }
    }

    public void applyYearCheck() {
        HttpBusiness.applyYearCheck(new YearCheckApplyReq(MacUtils.getMac(), mCardInfo.getCardNo(), mAts, mCardInfo.getCardRand(),cpuCardType,HttpBusiness.getTime()), mHandler);
    }

    public void setCardInfo(CardInfo info) {
        mCardInfo = info;
    }
}
