package com.bsit.pboard;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bsit.pboard.business.CardBusiness;
import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.MessageApplyWriteReq;
import com.bsit.pboard.model.MessageApplyWriteRes;
import com.bsit.pboard.model.MessageConfirmReq;
import com.bsit.pboard.model.MessageQueryReq;
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.utils.MacUtils;

import static com.bsit.pboard.constant.Constants.DEVICE_ID;


public class TestRecharge {
    private static final String TAG = TestRecharge.class.getName();
    private String reloadAmount;
    private String errorMsg;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "---------------rcv msg what--------------- = " + msg.what);
            switch (msg.what) {
                case 999:
                    removeMessages(999);
                    findCard();
                    break;
                case HttpBusiness.ERROR_CODE:
                    errorMsg = (String) msg.obj;
//                    changeViewByType(5);
                    break;
                case HttpBusiness.QUERY_ORDER_EROOR_CODE:
//                    changeViewByType(2);
                    break;
                case HttpBusiness.START_RECHARGECARD_EROOR_CODE:
//                    rechargeResult = false;
//                    changeViewByType(4);
                    String erroeCode = (String) msg.obj;
                    Log.i(TAG, "" + erroeCode);
                    break;
                case HttpBusiness.CONFIRM_RECHARGE_EROOR_CODE:
//                    rechargeResult = true;
//                    changeViewByType(4);
                    String erroeCode1 = (String) msg.obj;
                    Log.i(TAG, "" + erroeCode1);
                    break;
                case HttpBusiness.QUERY_ORDER_SUCEESS_CODE:
                    MessageQueryRes messageQueryRes = (MessageQueryRes) msg.obj;
//                    changeViewByType(3);
                    reloadAmount = messageQueryRes.getMoney();
                    tradeType = messageQueryRes.getTradetype();
                    outTradeNo = messageQueryRes.getOutTradeNo();
//                    rechargeId = messageQueryRes.getRechargeId();
                    topInit();
                    break;
                case HttpBusiness.START_RECHARGECARD_SUCEESS_CODE:
                    MessageApplyWriteRes messageApplyWriteRes = (MessageApplyWriteRes) msg.obj;
                    topRecharge(messageApplyWriteRes.getMAC2(), HttpBusiness.getDealStamp());
                    break;
                case HttpBusiness.CONFIRM_RECHARGE_SUCEESS_CODE:
//                    rechargeResult = true;
                    Log.e("MAIN", "2062成功");
//                    changeViewByType(4);
                    break;
            }
        }
    };

    private CardBusiness cardBusiness;
    private CardInfo mCardInfo;
    private String tradeType;
    private String outTradeNo;
    private Context mContext;

    public TestRecharge(Context context) {
        mContext = context;
        cardBusiness = CardBusiness.getInstance(context);
    }

    public void findCard() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String csn = cardBusiness.findCard();
                    if (!TextUtils.isEmpty(csn)) {
                        mCardInfo = cardBusiness.getCardInfo();
                        HttpBusiness.queryOrder(new MessageQueryReq(MacUtils.getMac(), HttpBusiness.getTime(), mCardInfo.getCardNo(), mCardInfo.getBalance()), mHandler);
                    }
                } catch (final CardBusiness.FindCardException e) {
                    mCardInfo = null;
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(999, 1000);
                } catch (final CardBusiness.ReadCardException e) {
                    mCardInfo = null;
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(999, 1000);
                }
            }
        }.start();
    }

    private void topInit() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    mCardInfo = cardBusiness.getTopInitInfo(mCardInfo, reloadAmount, DEVICE_ID, 0);
                    HttpBusiness.rechargeCard(new MessageApplyWriteReq(MacUtils.getMac(), mCardInfo.getCardNo(), "052", tradeType,
                            outTradeNo, mCardInfo.getCardRand(), mCardInfo.getCardTradeNo(), mCardInfo.getBalance(), reloadAmount,
                            mCardInfo.getQcMac(), "", "", HttpBusiness.getDealStamp()), mHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void topRecharge(final String mac2, final String messageDateTime) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    mCardInfo = cardBusiness.getTacFormTopUp(messageDateTime + mac2, mCardInfo, reloadAmount);
                    String writeFlag = TextUtils.isEmpty(mCardInfo.getTac()) ? "01" : "00";
                    Log.e("MAIN", "圈存结果：" + writeFlag);
//                    HttpBusiness.confirmRecharge(new MessageConfirmReq(deviceId, messageDateTime, cityCode, cardInfo.getCardNo(), cardInfo.getCardSeq(),
//                            reloadAmount, cardInfo.getBalance(), cardInfo.getCardMType(), cardInfo.getTac(), rechargeId, writeFlag), handler);

                    HttpBusiness.confirmRecharge(new MessageConfirmReq(MacUtils.getMac(), mCardInfo.getCardNo(), outTradeNo, "052", writeFlag, mCardInfo.getTac(), HttpBusiness.getDealStamp()), mHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("MAIN", "圈存错误：");
//                    handler.sendEmptyMessage(SHOW_FAILDRECHARGE_MSG_WHAT);
                }
            }
        }.start();
    }
}
