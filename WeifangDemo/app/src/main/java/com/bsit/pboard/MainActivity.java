package com.bsit.pboard;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsit.pboard.adapter.CardOperatorListener;
import com.bsit.pboard.adapter.Chandler;
import com.bsit.pboard.business.CardBusiness;
import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.MessageApplyWriteRes;
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.model.MonthTicketModifyRes;
import com.bsit.pboard.utils.ByteUtil;
import com.bsit.pboard.utils.CardOperator;
import com.bsit.pboard.utils.ConstantMsg;
import com.bsit.pboard.utils.ShellUtils;
import com.bsit.pboard.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.bsit.pboard.model.Constants.DEVICE_ID;


public class MainActivity extends Activity implements CardOperatorListener {
    private static final String TAG = MainActivity.class.getName();

    private TextView dateTimeTv;
    private TextView weekDayTv;
    private TextView terminalNoTv;
    private TextView warnmingTv;
    private TextView tipsMsgTv;
    private TextView tipsInfoTv;
    private ImageView signalIntensityIv;
    private ImageView image;
    private RelativeLayout resultRl;
    private LinearLayout warnmingLl;
    private LinearLayout welcomeLl;
    private LinearLayout cardInfoLl;
    private TextView tvCardNo;
    private TextView tvBalance;

    private TelephonyManager mTelManager;
    private MyPhoneStateListener mPhoneListener;
    private CardBusiness mCardBusiness;
    private CardInfo cardInfo;
    private MessageQueryRes messageQueryRes;
    private String reloadAmount;
    private String erroeCode;
    private String errorMsg;
    private String mOutTradeNo;
    private String mLastCardNo;
    private boolean hasSignIn;
    private boolean rechargeResult;

    private Handler mHandler;
    private CardOperator mCdOperator;
    private boolean isRecharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCardBusiness = CardBusiness.getInstance(this);
        mHandler = new Chandler(this);
        mCdOperator = new CardOperator(this, mHandler);
        initView();
        initLister();
        initTimeStamp();
        setWeekText();
    }

    private void initView() {
        dateTimeTv = findViewById(R.id.date_time_tv);
        weekDayTv = findViewById(R.id.week_day_tv);
        terminalNoTv = findViewById(R.id.terminal_no_tv);
        warnmingTv = findViewById(R.id.warnming_tv);
        tipsMsgTv = findViewById(R.id.tips_loading_msg);
        tipsInfoTv = findViewById(R.id.tips_loading_info);
        signalIntensityIv = findViewById(R.id.signal_intensity_iv);
        image = findViewById(R.id.image);
        resultRl = findViewById(R.id.result_ll);
        warnmingLl = findViewById(R.id.warnming_ll);
        welcomeLl = findViewById(R.id.welcome_ll);
        cardInfoLl = findViewById(R.id.card_info_ll);
        tvCardNo = findViewById(R.id.tv_card_no);
        tvBalance = findViewById(R.id.tv_balance);
    }

    private void initTimeStamp() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        dateTimeTv.setText(dff.format(new Date()));
        mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_UPDATE_TIME, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
    }

    private void initLister() {
        mPhoneListener = new MyPhoneStateListener();
        mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        assert mTelManager != null;
        mTelManager.listen(mPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private void setWeekText() {
        Date date = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        weekDayTv.setText(dateFm.format(date));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mTelManager.listen(mPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        if (!HttpBusiness.isNetworkAvailable(MainActivity.this)) {
            changeViewByType(ConstantMsg.VIEW_INIT_NETWORK);
        } else {
            if (!hasSignIn) mCdOperator.signIn();
        }
    }


    /**
     * 网络检测
     */
    private void netChecker() {
        if (!HttpBusiness.isNetworkAvailable(MainActivity.this)) {
            ShellUtils.execCommand("./etc/ppp/init.quectel-pppd &", true);
            mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_INIT_NETWORK, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
        } else {
            mHandler.removeMessages(ConstantMsg.VIEW_INIT_NETWORK);
            mCdOperator.signIn();
//            changeViewByType(ConstantMsg.MSG_FIND_CARD);
        }
    }

    private void findCard() {
        mCdOperator.findCard(isRecharge);
//        cardInfo = mCdOperator.testFindCard();
    }

    /**
     * 圈存初始化
     */
    private void topInit() {
        mCdOperator.rechargeInit(messageQueryRes.getTradetype(), messageQueryRes.getMoney(), DEVICE_ID, messageQueryRes.getOutTradeNo());
//        mCdOperator.testRechargeInit("", DEVICE_ID, "");
    }


    private void monthTickeyModify(){
        mCdOperator.monthTicketModify(messageQueryRes.getOutTradeNo());
    }

    /**
     * 圈存
     */
    private void topRecharge(final String mac2, final String messageDateTime) {
        try {
            Log.i(TAG, "to recharge !! messagedatatime = " + messageDateTime + ", mac2 = " + mac2);
            cardInfo = mCardBusiness.getTacFormTopUp(messageDateTime + mac2, cardInfo, reloadAmount);
            String writeFlag = TextUtils.isEmpty(cardInfo.getTac()) ? "01" : "00";
            Log.e(TAG, "圈存结果：" + writeFlag);
            mCdOperator.setCardInfo(cardInfo);
            mCdOperator.rechargeConfirm(mOutTradeNo, writeFlag);
        } catch (Exception e) {
            rechargeResult = false;
            erroeCode = e.getMessage();
            Log.e(TAG, "圈存错误：" + erroeCode);
//            changeViewByType(ConstantMsg.VIEW_CHARGE_END);
        }
    }

    private void testTopRecharge(final String mac2, final String messageDateTime) {
        try {
            Log.i(TAG, "to recharge !! messagedatatime = " + messageDateTime + ", mac2 = " + mac2);
//            cardInfo = mCardBusiness.getTacFormTopUp(messageDateTime + mac2, cardInfo, reloadAmount);
            cardInfo = mCardBusiness.testGetTacFormTopUp(messageDateTime + mac2, cardInfo, reloadAmount);
            String writeFlag = TextUtils.isEmpty(cardInfo.getTac()) ? "01" : "00";
            Log.e(TAG, "圈存结果：" + writeFlag);
            mCdOperator.setCardInfo(cardInfo);
            mCdOperator.rechargeConfirm(mOutTradeNo, writeFlag);
        } catch (Exception e) {
            rechargeResult = false;
            erroeCode = e.getMessage();
            Log.e(TAG, "圈存错误：" + erroeCode);
//            changeViewByType(ConstantMsg.VIEW_CHARGE_END);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTelManager.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
    }

/*    private void initSetting() {
        Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, 1);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone("Asia/Shanghai");
    }*/

    private void changeViewByType(int type) {
        cardInfoLl.setVisibility(View.GONE);
        switch (type) {
            case ConstantMsg.VIEW_STICK_CARD:
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.GONE);
                cardInfoLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.VISIBLE);
                String snId = getSharedPreferences("deviceInfo", MODE_PRIVATE).getString("snId", "");
                if (TextUtils.isEmpty(snId)) {
                    try {
                        snId = mCardBusiness.getSN();
                        getSharedPreferences("deviceInfo", MODE_PRIVATE).edit().putString("sbId", snId).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(snId)) {
                    snId = Build.SERIAL;
                }
                terminalNoTv.setText(snId);
                mHandler.sendEmptyMessage(ConstantMsg.MSG_FIND_CARD);
                break;
            case ConstantMsg.VIEW_INVALID_CARD:
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_useless_card));
                welcomeLl.setVisibility(View.GONE);
                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
                break;
            case ConstantMsg.VIEW_NO_ORDER:
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
//                warnmingTv.setText(getString(R.string.str_not_check_uncharge_order));
                warnmingTv.setText(errorMsg);
                welcomeLl.setVisibility(View.GONE);
//                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD,ConstantMsg.TIME_INTEVAL_ONE_SECOND);
                break;
            case ConstantMsg.VIEW_CHARAGING:
                resultRl.setVisibility(View.VISIBLE);
                warnmingLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.GONE);
                showLoading(getString(R.string.str_recharging), getString(R.string.str_no_moving_card));
                break;
            case ConstantMsg.VIEW_CHARGE_END:
                image.clearAnimation();
                resultRl.setVisibility(View.VISIBLE);
                warnmingLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.GONE);
                if (rechargeResult) {
                    image.setImageResource(R.drawable.icon_budnegchengong);
                    tipsMsgTv.setText(getResources().getString(R.string.str_recharge_success_balance) + ByteUtil.toAmountString(ByteUtil.pasInt(cardInfo.getBalance()) / 100.0f));
                    tipsInfoTv.setText(getString(R.string.str_please_move_card));
                } else {
                    image.setImageResource(R.drawable.icon_budnegshibai);
                    tipsMsgTv.setText(getResources().getString(R.string.str_error_code) + erroeCode);
//                    tipsInfoTv.setText(getString(R.string.str_recharge_fail_please_retry_stick_card));
                    tipsInfoTv.setText(errorMsg);
                }
                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_THREE_SECOND);
                isNeedContinueRecharge(cardInfo.getCardNo());
                break;
            case ConstantMsg.VIEW_NET_ERROR:
                if (cardInfo == null) {
                    resultRl.setVisibility(View.GONE);
                    warnmingTv.setText(getResources().getString(R.string.wifi_disabled_network_failure));
                    warnmingLl.setVisibility(View.VISIBLE);
                    welcomeLl.setVisibility(View.GONE);
                } else {
                    resultRl.setVisibility(View.GONE);
                    warnmingLl.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(errorMsg)) {
                        errorMsg = getString(R.string.str_unknow_error);
                    }
                    warnmingTv.setText(errorMsg);
                    welcomeLl.setVisibility(View.GONE);
                }
                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
                break;
            case ConstantMsg.VIEW_SHOW_CARD:
                welcomeLl.setVisibility(View.GONE);
                cardInfoLl.setVisibility(View.VISIBLE);
                tvCardNo.setText(cardInfo.getCardNo());
                if (cardInfo.getBalance() == null) {
                    tvBalance.setText("0");
                } else {
                    tvBalance.setText(ByteUtil.toAmountString(ByteUtil.pasInt(cardInfo.getBalance()) / 100.0f));
                }
                break;
            case ConstantMsg.VIEW_CARD_NOT_USE:
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_no_permitted_card));
                welcomeLl.setVisibility(View.GONE);
                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_ONE_SECOND);
                break;
            case ConstantMsg.VIEW_INIT_NETWORK:
                welcomeLl.setVisibility(View.GONE);
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_init_network));
                netChecker();
                break;
            default:
                break;
        }
    }

    private void isNeedContinueRecharge(String cardNo) {
        if (mLastCardNo == null) {
            mLastCardNo = cardNo;
        } else {
            if (cardNo.equals(mLastCardNo)) {
                isRecharge = true;
            } else {
                isRecharge = false;
            }
        }
    }

    private void showLoading(String msg, String info) {
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_loading);
        image.setImageResource(R.drawable.animation);
        image.startAnimation(hyperspaceJumpAnimation);
        tipsMsgTv.setText(msg);
        tipsInfoTv.setText(info);
    }

    @Override
    public void onCardOperate(int type, Object response) {
        switch (type) {
            case ConstantMsg.MSG_FIND_CARD:
                findCard();
                break;
            case HttpBusiness.ERROR_CODE:
                errorMsg = (String) response;
                changeViewByType(ConstantMsg.VIEW_NET_ERROR);
                break;
            case HttpBusiness.SIGN_IN_SUCEESS_CODE:
                hasSignIn = true;
                changeViewByType(ConstantMsg.VIEW_STICK_CARD);
                break;
            case HttpBusiness.SIGN_IN_EROOR_CODE:
                hasSignIn = false;
                changeViewByType(ConstantMsg.VIEW_STICK_CARD);
                break;
            case HttpBusiness.START_RECHARGECARD_SUCEESS_CODE:
                if (messageQueryRes.getTradetype().equals("01")){

                }
                MessageApplyWriteRes messageApplyWriteRes = (MessageApplyWriteRes) response;
                topRecharge(messageApplyWriteRes.getMAC2(), HttpBusiness.getDealStamp());
                if (messageQueryRes.getTradetype().equals("02")){

                }
                break;
            case HttpBusiness.CONFIRM_RECHARGE_MONTH_SUCEESS_CODE:
                MonthTicketModifyRes monthTicket = (MonthTicketModifyRes) response;
                String apdu = monthTicket.getApdu();
                Log.i(TAG, "apdu = " + apdu);
//                updateMonthTicket();
                break;
            case HttpBusiness.START_RECHARGECARD_EROOR_CODE:
            case HttpBusiness.CONFIRM_RECHARGE_MONTH_EROOR_CODE:
                erroeCode = (String) response;
//                rechargeResult = false;
                changeViewByType(ConstantMsg.VIEW_CHARGE_END);
                break;
            case HttpBusiness.CONFIRM_RECHARGE_SUCEESS_CODE:
                rechargeResult = true;
                Log.e(TAG, "recharge 2062 成功");
                changeViewByType(ConstantMsg.VIEW_CHARGE_END);
                break;
            case HttpBusiness.CONFIRM_RECHARGE_EROOR_CODE:
                rechargeResult = false;
                changeViewByType(ConstantMsg.VIEW_CHARGE_END);
                break;
            case HttpBusiness.QUERY_ORDER_SUCEESS_CODE:


                mHandler.removeMessages(ConstantMsg.MSG_FIND_CARD);

                messageQueryRes = (MessageQueryRes) response;
                changeViewByType(ConstantMsg.VIEW_CHARAGING);
                reloadAmount = messageQueryRes.getMoney();
                mOutTradeNo = messageQueryRes.getOutTradeNo();
                topInit();
                break;
            case HttpBusiness.QUERY_ORDER_EROOR_CODE:
                errorMsg = (String) response;
                changeViewByType(ConstantMsg.VIEW_NO_ORDER);
                break;
            case ConstantMsg.MSG_FAIL_READ_CARD:
                changeViewByType(ConstantMsg.VIEW_INVALID_CARD);
                break;
            case ConstantMsg.MSG_FAIL_RECHARGE:
                Log.i(TAG, "fail recharg .........");
                rechargeResult = false;
                changeViewByType(ConstantMsg.VIEW_CHARGE_END);
                break;
            case ConstantMsg.MSG_FAIL_RECHARGE_CONFIRM:
                break;
            case ConstantMsg.VIEW_SHOW_CARD:
                cardInfo = (CardInfo) response;
                changeViewByType(ConstantMsg.VIEW_SHOW_CARD);
                break;
            case ConstantMsg.VIEW_CARD_NOT_USE:
                changeViewByType(ConstantMsg.VIEW_CARD_NOT_USE);
                break;
            case ConstantMsg.MSG_UPDATE_TIME:
                initTimeStamp();
                break;
            case ConstantMsg.VIEW_INIT_NETWORK:
                changeViewByType(ConstantMsg.VIEW_INIT_NETWORK);
                break;
            case ConstantMsg.VIEW_STICK_CARD:
                changeViewByType(ConstantMsg.VIEW_STICK_CARD);
                break;
            case ConstantMsg.VIEW_UNRECOGNIZE_CARD:
                ToastUtils.showToast(getString(R.string.str_card_not_distinguish), MainActivity.this);
                changeViewByType(ConstantMsg.VIEW_STICK_CARD);
                break;
            case ConstantMsg.MSG_HAS_RECHERGED:
                changeViewByType(ConstantMsg.VIEW_SHOW_CARD);
                break;
//            case HttpBusiness.HEART_BEAT_SUCEESS_CODE:
//                UpdateUtils.upVersion((Rda) response);
//                break;

            case HttpBusiness.TEST_ERROR:
                String error = (String) response;
                Log.i(TAG, "test error = " + error);
                break;
            case HttpBusiness.TEST_OK:
                String mac2 = (String) response;
                Log.i(TAG, "test ok = " + mac2);
//                topRecharge(mac2, HttpBusiness.getDealStamp());
//                testTopRecharge(mac2,HttpBusiness.getDealStamp());
                break;
            case 555:
                Log.i(TAG, "555 555---");
//                topInit();
                break;
            default:
                break;
        }
    }

    /* 开始PhoneState */
    private class MyPhoneStateListener extends PhoneStateListener {
        /* 从得到的信号强度,每个tiome供应商有更新 */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int asu = signalStrength.getGsmSignalStrength();
            if (asu <= 2 || asu == 99) {
                signalIntensityIv.setImageResource(R.drawable.signal_intensity0);
            } else if (asu >= 12) {
                signalIntensityIv.setImageResource(R.drawable.signal_intensity4);
            } else if (asu >= 8) {
                signalIntensityIv.setImageResource(R.drawable.signal_intensity3);
            } else if (asu >= 5) {
                signalIntensityIv.setImageResource(R.drawable.signal_intensity2);
            } else {
                signalIntensityIv.setImageResource(R.drawable.signal_intensity1);
            }
        }
    }
}
