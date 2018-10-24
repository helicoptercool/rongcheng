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
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.model.MonthTicketModifyReq;
import com.bsit.pboard.model.RechargeConfirmParm;
import com.bsit.pboard.model.RechargeInitParm;

public class CardOperator {
    private static final String TAG = CardOperator.class.getName();
    private Context mContext;
    private Handler mHandler;
    private CardInfo mCardInfo;
    private RechargeInitParm mRechargeInitParm;
    private RechargeConfirmParm mRechConfParm;
    private int mCardType;
    private String cpuCardType = "052";

    public CardOperator(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    private void doFindCard() {
        //交通部
        int CARD_TRANSPORT = 200;
        try {
            String cardSn = CardBusiness.findCard();
            if (!TextUtils.isEmpty(cardSn)) {
                if (mCardType == CARD_TRANSPORT) {
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
            if (mCardType == CARD_TRANSPORT) {
                //住建部
                mCardType = 100;
            } else {
                mCardType = CARD_TRANSPORT;
            }
            mHandler.sendEmptyMessageDelayed(ConstantMsg.FIND_CARD_MSG_WHAT, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRechargeInit() {
        try {
            mCardInfo = CardBusiness.getTopInitInfo(mCardInfo, mRechargeInitParm.getReloadAmount(), mRechargeInitParm.getDeviceId());
//            MessageQueryRes messageQueryRes = mRechargeInitParm.getMessageQueryRes();
            if (mRechargeInitParm.getTradeType().equals("01")) { //01 IC卡充值
                HttpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), mCardInfo.getCardNo(), cpuCardType, mRechargeInitParm.getTradeType(),
                        "201810231006261865", mCardInfo.getCardRand(), mCardInfo.getCardSeq(), mCardInfo.getBalance(), mRechargeInitParm.getReloadAmount(),
                        mCardInfo.getQcMac(), "", "", HttpBusiness.getTime()), mHandler);
            } else if (mRechargeInitParm.getTradeType().equals("02")) { //02月票充值
                String data0015 = CardBusiness.getData0015();
                String ats = "";
                if (data0015.length() > 16) {
                    ats = data0015.substring(data0015.length() - 8, data0015.length());
                }
                HttpBusiness.monthTicketModify(new MonthTicketModifyReq(MacUtils.getMac(), mCardInfo.getCardNo(), "201810231006261865", cpuCardType, data0015, ats, mCardInfo.getCardRand()), mHandler);
//                        httpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), cardInfo.getCardNo(), cardType, messageQueryRes.getTradetype(),
//                                messageQueryRes.getOutTradeNo(), cardInfo.getCardRand(), cardInfo.getCardSeq(), cardInfo.getBalance(), messageQueryRes.getMoney(), cardInfo.getQcMac(), "", "", HttpBusiness.getTime()), handler);
            }
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.SHOW_FAILDRECHARGE_MSG_WHAT);
        }
    }

    private void doRechargeConfirm() {
        try {
            HttpBusiness.confirmRecharge(new MessageConfirmReq(MacUtils.getMac(), mCardInfo.getCardNo(), mRechConfParm.getOutTradeNo(), cpuCardType, mRechConfParm.getWriteCardStatus(), mCardInfo.getTac()), mHandler);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(ConstantMsg.SHOW_FAILDRECHARGE_MSG_WHAT);
        }
    }

    public void signIn() {
        HttpBusiness.signIn(mHandler);
    }

    public void findCard() {
        doFindCard();
    }

    public void rechargeInit(RechargeInitParm parm) {
        mRechargeInitParm = parm;
        doRechargeInit();
    }

    public void rechargeConfirm(RechargeConfirmParm parm) {
        mRechConfParm = parm;
        doRechargeConfirm();
    }

    public void setCardInfo(CardInfo info) {
        mCardInfo = info;
    }
}
