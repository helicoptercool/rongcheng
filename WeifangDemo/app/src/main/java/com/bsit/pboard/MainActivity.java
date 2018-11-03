package com.bsit.pboard;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bsit.pboard.adapter.YearCheckListener;
import com.bsit.pboard.business.CardBusiness;
import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.constant.ConstantMsg;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.HeartBeatReq;
import com.bsit.pboard.model.HeartBeatRsp;
import com.bsit.pboard.model.MessageApplyWriteRes;
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.model.MonthTicketModifyRes;
import com.bsit.pboard.model.YearCheckApplyRsp;
import com.bsit.pboard.model.YearCheckQueryRsp;
import com.bsit.pboard.utils.ByteUtil;
import com.bsit.pboard.utils.CardOperator;
import com.bsit.pboard.utils.MacUtils;
import com.bsit.pboard.utils.ShellUtils;
import com.bsit.pboard.utils.ToastUtils;
import com.bsit.pboard.utils.UpdateUtils;
import com.bsit.pboard.utils.VoiceUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.bsit.pboard.constant.Constants.DEVICE_ID;


public class MainActivity extends Activity implements CardOperatorListener, YearCheckListener {
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
    private String reloadAmount;
    private String erroeCode;
    private String errorMsg;
    private String mOutTradeNo;
    private String mLastCardNo;
    private String mTradeType;
    private boolean mHasSignIn;
    private boolean mHasCharged;
    private boolean rechargeResult;

    private Handler mHandler;
    private CardOperator mCardOperator;
    private String snId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCardBusiness = CardBusiness.getInstance(this);
        mHandler = new Chandler(this);
        ((Chandler) mHandler).setYearCheckListener(this);
        mCardOperator = new CardOperator(this, mHandler);
        initView();
//        initSetting();
        initLister();
        initTimeStamp();
//        setWeekText();
        setSnId();
        mHandler.sendEmptyMessage(ConstantMsg.MSG_HEART_BEAT);
    }

    private void initView() {
        dateTimeTv = (TextView) findViewById(R.id.date_time_tv);
        weekDayTv = (TextView) findViewById(R.id.week_day_tv);
        weekDayTv.setVisibility(View.GONE);
        terminalNoTv = (TextView) findViewById(R.id.terminal_no_tv);
        warnmingTv = (TextView) findViewById(R.id.warnming_tv);
        tipsMsgTv = (TextView) findViewById(R.id.tips_loading_msg);
        tipsInfoTv = (TextView) findViewById(R.id.tips_loading_info);
        signalIntensityIv = (ImageView) findViewById(R.id.signal_intensity_iv);
        image = (ImageView) findViewById(R.id.image);
        resultRl = (RelativeLayout) findViewById(R.id.result_ll);
        warnmingLl = (LinearLayout) findViewById(R.id.warnming_ll);
        welcomeLl = (LinearLayout) findViewById(R.id.welcome_ll);
        cardInfoLl = (LinearLayout) findViewById(R.id.card_info_ll);
        tvCardNo = (TextView) findViewById(R.id.tv_card_no);
        tvBalance = (TextView) findViewById(R.id.tv_balance);
    }

    private void initTimeStamp() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss  EEEE",Locale.CHINESE);
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        dateTimeTv.setText(dff.format(new Date()));
        timeHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_UPDATE_TIME, ConstantMsg.TIME_INTEVAL_INIT_TIME);
    }

    private void initLister() {
        mPhoneListener = new MyPhoneStateListener();
        mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelManager != null) {
            mTelManager.listen(mPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    private void setWeekText() {
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE",Locale.CHINESE);
        dateFm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date();
        weekDayTv.setText(dateFm.format(date));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mTelManager.listen(mPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        if (!HttpBusiness.isNetworkAvailable(MainActivity.this)) {
            changeViewByType(ConstantMsg.VIEW_INIT_NETWORK);
            //netChecker();
        } else {
            if (!mHasSignIn) mCardOperator.signIn();
        }
    }

    private void listenUpgrade() {

        String deviceId = DEVICE_ID;
        String supplierNo = "";
        String dateTime = UpdateUtils.getTime();
        String merchantNo = "";
        String posId = "";
        String termNo = MacUtils.getMac();
        String samId = "";
        String binVer = UpdateUtils.getVerName(this);
        String binDate = "";
        String whiteVer = "";
        String regionCode = "";
        String dataCounts = "";
        String latestTradeTime = "";
        String latestBootTime = "";
        HeartBeatReq heartBeatReq = new HeartBeatReq();
        heartBeatReq.setDeviceId(deviceId);
        heartBeatReq.setSupplierNo(supplierNo);
        heartBeatReq.setDateTime(dateTime);
        heartBeatReq.setMerchantNo(merchantNo);
        heartBeatReq.setPosId(posId);
        heartBeatReq.setTermNo(termNo);
        heartBeatReq.setSamId(samId);
        heartBeatReq.setBinVer(binVer);
        heartBeatReq.setBinDate(binDate);
        heartBeatReq.setWhiteVer(whiteVer);
        heartBeatReq.setRegionCode(regionCode);
        heartBeatReq.setDataCounts(dataCounts);
        heartBeatReq.setLatestTradeTime(latestTradeTime);
        heartBeatReq.setLatestBootTime(latestBootTime);

        HttpBusiness.heartBeat(heartBeatReq, mHandler);
        mHandler.sendEmptyMessageDelayed(ConstantMsg.MSG_HEART_BEAT, ConstantMsg.TIME_INTEVAL_HEART_BEAT);
    }


    /**
     * 网络检测
     */
    private void netChecker() {
        if (!HttpBusiness.isNetworkAvailable(MainActivity.this)) {
//            ShellUtils.execCommand("./etc/ppp/init.quectel-pppd &", true);
            ShellUtils.execCommand("/system/bin/dongle_test 1", false);
            mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_INIT_NETWORK, ConstantMsg.TIME_INTEVAL_NET_CHECK);
        } else {
            mHandler.removeMessages(ConstantMsg.VIEW_INIT_NETWORK);
            changeViewByType(ConstantMsg.VIEW_NETWORK_OK);
            mCardOperator.signIn();
        }
    }

    private void setSnId() {
        snId = getSharedPreferences("deviceInfo", MODE_PRIVATE).getString("snId", "");
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
    }

    private void findCard() {
        mCardOperator.findCard(mLastCardNo);
    }

    /**
     * 圈存初始化
     */
    private void topInit() {
        mCardOperator.rechargeInit(mTradeType, reloadAmount, DEVICE_ID, mOutTradeNo);
    }


    private void monthTickeyModify() {
        mCardOperator.monthTicketModify(mOutTradeNo);
    }

    private void sendMonthPdu(String[] pdus) {
        mCardOperator.sendMonthPdu(pdus, false, mOutTradeNo);
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
            mCardOperator.setCardInfo(cardInfo);
            mCardOperator.rechargeConfirm(mOutTradeNo, writeFlag);
        } catch (Exception e) {
            rechargeResult = false;
            erroeCode = e.getMessage();
            Log.e(TAG, "圈存错误：" + erroeCode);
            changeViewByType(ConstantMsg.VIEW_CHARGE_END);
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
                mHandler.sendEmptyMessage(ConstantMsg.MSG_FIND_CARD);
                break;
            case ConstantMsg.VIEW_INVALID_CARD:
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_useless_card));
                welcomeLl.setVisibility(View.GONE);
                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_FIND_CARD);
                break;
            case ConstantMsg.VIEW_NO_ORDER:
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
//                warnmingTv.setText(getString(R.string.str_not_check_uncharge_order));
                warnmingTv.setText(errorMsg);
                welcomeLl.setVisibility(View.GONE);
                VoiceUtils.with(this).play(R.raw.not_query_recharge_order);
                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_THREE_SECOND);
                break;
            case ConstantMsg.VIEW_CHARAGING:
                resultRl.setVisibility(View.VISIBLE);
                warnmingLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.GONE);
                VoiceUtils.with(this).play(R.raw.recharging_not_move_card);
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
                mLastCardNo = cardInfo.getCardNo();
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
                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_NET_CHECK);
                break;
            case ConstantMsg.VIEW_SHOW_CARD:
                welcomeLl.setVisibility(View.GONE);
                cardInfoLl.setVisibility(View.VISIBLE);
                tvCardNo.setText(cardInfo.getCardNo());
                if (cardInfo.getBalance() == null) {
                    tvBalance.setText("0");
                    VoiceUtils.with(this).play("0", VoiceUtils.GET_BALANCE_SUCCESS);
                } else {
                    String balance = ByteUtil.toAmountString(ByteUtil.pasInt(cardInfo.getBalance()) / 100.0f);
                    tvBalance.setText(ByteUtil.toAmountString(ByteUtil.pasInt(cardInfo.getBalance()) / 100.0f));

                    VoiceUtils.with(this).play(balance, VoiceUtils.GET_BALANCE_SUCCESS);
                }
                if (mHasCharged) {
                    ToastUtils.showToast(getString(R.string.str_has_recharged), this);
                    mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_THREE_SECOND);
                }
                break;
            case ConstantMsg.VIEW_CARD_NOT_USE:
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_no_permitted_card));
                welcomeLl.setVisibility(View.GONE);
                mHandler.sendEmptyMessageDelayed(ConstantMsg.VIEW_STICK_CARD, ConstantMsg.TIME_INTEVAL_FIND_CARD);
                break;
            case ConstantMsg.VIEW_INIT_NETWORK:
                welcomeLl.setVisibility(View.GONE);
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_init_network));
//                netChecker();
                break;
            case ConstantMsg.VIEW_NETWORK_OK:
                welcomeLl.setVisibility(View.GONE);
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_init_network_ok));
                break;
            default:
                break;
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
                mHasSignIn = true;
                changeViewByType(ConstantMsg.VIEW_STICK_CARD);
                break;
            case HttpBusiness.SIGN_IN_EROOR_CODE:
                mHasSignIn = false;
//                changeViewByType(ConstantMsg.VIEW_STICK_CARD);
                break;
            case HttpBusiness.START_RECHARGECARD_SUCEESS_CODE:
                if (mTradeType.equals(ConstantMsg.TRADE_TYPE_WALLET)) {
                    MessageApplyWriteRes messageApplyWriteRes = (MessageApplyWriteRes) response;
                    topRecharge(messageApplyWriteRes.getMAC2(), HttpBusiness.getDealStamp());
                } else if (mTradeType.equals(ConstantMsg.TRADE_TYPE_MONTH_TICKET)) {
                    monthTickeyModify();
                }
                break;
            case HttpBusiness.CONFIRM_RECHARGE_MONTH_SUCEESS_CODE:
                MonthTicketModifyRes monthTicket = (MonthTicketModifyRes) response;
                String apdu = monthTicket.getApdu();
                Log.i(TAG, "apdu = " + apdu);
                String[] pdus = apdu.split(",");
                sendMonthPdu(pdus);
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
//                mHandler.removeMessages(ConstantMsg.MSG_FIND_CARD);
                MessageQueryRes messageQueryRes = (MessageQueryRes) response;
                changeViewByType(ConstantMsg.VIEW_CHARAGING);
                mTradeType = messageQueryRes.getTradetype();
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
            case ConstantMsg.MSG_FAIL_RECHARGE_CONFIRM:
                Log.i(TAG, "fail recharg .........");
                errorMsg = getString(R.string.str_recharge_init_failure);
                rechargeResult = false;
                changeViewByType(ConstantMsg.VIEW_CHARGE_END);
                break;
            case ConstantMsg.VIEW_SHOW_CARD:
                mHasCharged = false;
                cardInfo = (CardInfo) response;
                changeViewByType(ConstantMsg.VIEW_SHOW_CARD);
                break;
            case ConstantMsg.VIEW_CARD_NOT_USE:
                changeViewByType(ConstantMsg.VIEW_CARD_NOT_USE);
                break;
            case ConstantMsg.VIEW_INIT_NETWORK:
//                changeViewByType(ConstantMsg.VIEW_INIT_NETWORK);
               // netChecker();
                break;
            case ConstantMsg.VIEW_STICK_CARD:
                changeViewByType(ConstantMsg.VIEW_STICK_CARD);
                break;
            case ConstantMsg.VIEW_UNRECOGNIZE_CARD:
                ToastUtils.showToast(getString(R.string.str_card_not_distinguish), MainActivity.this);
                changeViewByType(ConstantMsg.VIEW_STICK_CARD);
                break;
            case ConstantMsg.MSG_HAS_RECHERGED:
                mHasCharged = true;
                changeViewByType(ConstantMsg.VIEW_SHOW_CARD);
                break;
            case ConstantMsg.MSG_CARD_CHANGE_TYPE:
                mLastCardNo = "";
                break;
            case HttpBusiness.HEART_BEAT_SUCEESS_CODE:
                UpdateUtils.upVersion((HeartBeatRsp) response);
                break;
            case ConstantMsg.MSG_HEART_BEAT:
                listenUpgrade();
                break;
            default:
                break;
        }
    }

    @Override
    public void onYearCheck(int type, String status, Object result) {
        switch (type) {
            case ConstantMsg.TYPE_YEAR_CHECK_QUERY:
                if (status.equals(ConstantMsg.TYPE_SUCCESS)) {
                    YearCheckQueryRsp yearCheckQueryRsp = (YearCheckQueryRsp) result;
                    String isActivation = yearCheckQueryRsp.getStatus(); //0 未激活 1 已激活
                    String validityPeriod = yearCheckQueryRsp.getValidityPeriod();
                    mCardOperator.applyYearCheck();
                } else {

                }
                break;
            case ConstantMsg.TYPE_YEAR_CHECK_APPLY:
                if (status.equals(ConstantMsg.TYPE_SUCCESS)) {
                    YearCheckApplyRsp yearCheckApplyRsp = (YearCheckApplyRsp) result;
                    String apdus = yearCheckApplyRsp.getApdu();
                    String[] apdu = apdus.split(",");
                    mCardOperator.sendPdus(apdu);
                } else {

                }
                break;
            case ConstantMsg.TYPE_YEAR_CHECK_NOTICE:
                if (status.equals(ConstantMsg.TYPE_SUCCESS)) {
                    //00000为成功，其他为异常（长度5）
                } else {

                }
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


    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantMsg.MSG_UPDATE_TIME:
                    initTimeStamp();
                    break;
            }
        }
    };
}
