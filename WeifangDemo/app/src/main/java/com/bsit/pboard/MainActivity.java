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

import com.bsit.pboard.business.CardBusiness;
import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.MessageApplyWriteRes;
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.model.MonthTicketModifyRes;
import com.bsit.pboard.model.Rda;
import com.bsit.pboard.model.RechargeConfirmParm;
import com.bsit.pboard.model.RechargeInitParm;
import com.bsit.pboard.utils.ByteUtil;
import com.bsit.pboard.utils.CardOperator;
import com.bsit.pboard.utils.ConstantMsg;
import com.bsit.pboard.utils.MacUtils;
import com.bsit.pboard.utils.ShellUtils;
import com.bsit.pboard.utils.ToastUtils;
import com.bsit.pboard.utils.UpdateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity {
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
    private CardBusiness cardBusiness;
    private CardInfo cardInfo;
    private MessageQueryRes messageQueryRes;
    private String reloadAmount;
    private String erroeCode;
    private String errorMsg;
    private SimpleDateFormat dff;
    private String mOutTradeNo;
    private boolean hasSignIn;
    private boolean rechargeResult;

    private Handler handler = new Mhandler();
    private CardOperator mCdOperator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardBusiness = CardBusiness.getInstance(this);
        mCdOperator = new CardOperator(this, handler);
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
        dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        dateTimeTv.setText(dff.format(new Date()));
        handler.sendEmptyMessageDelayed(ConstantMsg.UPDATE_TIME_MSG_WHAT, 1000);
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
        initTimeStamp();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mTelManager.listen(mPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        changeViewByType(ConstantMsg.INIT_NETWORK);
        if (!HttpBusiness.isNetworkAvailable(MainActivity.this)) {
            handler.sendEmptyMessage(ConstantMsg.INIT_NETWORK);
        } else {
            mCdOperator.signIn();
            handler.sendEmptyMessage(ConstantMsg.SHOW_WELCOME_MSG_WHAT);
        }
    }


    /**
     * 网络检测
     */
    private void netChecker() {
        if (!HttpBusiness.isNetworkAvailable(MainActivity.this)) {
            ShellUtils.execCommand("./etc/ppp/init.quectel-pppd &", true);
            handler.sendEmptyMessageDelayed(ConstantMsg.INIT_NETWORK, 2000);
        } else {
            handler.removeMessages(ConstantMsg.INIT_NETWORK);
            mCdOperator.signIn();
            handler.sendEmptyMessage(ConstantMsg.FIND_CARD_MSG_WHAT);
        }
    }

    private void findCard() {
        mCdOperator.findCard();
    }

    /**
     * 圈存初始化
     */
    private void topInit(String tradeType, String money) {
        //"tradetype":"01","money":"100","startdate":"","enddate":"","datedif":"","outTradeNo":"201810231006261865"
        tradeType = "01";
        money = "100";
        RechargeInitParm parm = new RechargeInitParm(tradeType, money, MacUtils.getMac(), messageQueryRes);
        mCdOperator.rechargeInit(parm);
    }

    /**
     * 圈存
     *
     */
    /*private void topRecharge(final String mac2, final String messageDateTime) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    cardInfo = cardBusiness.getTacFormTopUp(messageDateTime + mac2, cardInfo, reloadAmount);
                    String writeFlag = TextUtils.isEmpty(cardInfo.getTac()) ? "01" : "00";
                    Log.e("MAIN", "圈存结果：" + writeFlag);
                    httpBusiness.confirmRecharge(new MessageConfirmReq(MacUtils.getMac(), cardInfo.getCardSeq(), mOutTradeNo, cpuCardType, mWriteCardStatus, cardInfo.getTac()), handler);
                } catch (Exception e) {
                    rechargeResult = false;
                    erroeCode = e.getMessage();
                    Log.e("MAIN", "圈存错误：" + erroeCode);
                    handler.sendEmptyMessage(ConstantMsg.SHOW_FAILDRECHARGE_MSG_WHAT);
                }
            }
        }.start();
    }*/

    /**
     * 圈存
     */
    private void topRecharge(final String mac2, final String messageDateTime) {
        try {
            cardInfo = CardBusiness.getTacFormTopUp(messageDateTime + mac2, cardInfo, reloadAmount);
            String writeFlag = TextUtils.isEmpty(cardInfo.getTac()) ? "01" : "00";
            Log.e("MAIN", "圈存结果：" + writeFlag);
            mCdOperator.setCardInfo(cardInfo);
            mCdOperator.rechargeConfirm(new RechargeConfirmParm(mOutTradeNo, writeFlag));
//            httpBusiness.confirmRecharge(new MessageConfirmReq(MacUtils.getMac(), cardInfo.getCardSeq(), mOutTradeNo, cpuCardType, mWriteCardStatus, cardInfo.getTac()), handler);
        } catch (Exception e) {
            rechargeResult = false;
            erroeCode = e.getMessage();
            Log.e("MAIN", "圈存错误：" + erroeCode);
            handler.sendEmptyMessage(ConstantMsg.SHOW_FAILDRECHARGE_MSG_WHAT);
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
            case 0: //欢迎界面
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.GONE);
                cardInfoLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.VISIBLE);
                String snId = getSharedPreferences("deviceInfo", MODE_PRIVATE).getString("snId", "");
                if (TextUtils.isEmpty(snId)) {
                    try {
                        snId = cardBusiness.getSN();
                        Log.i(TAG, "sn no ============ " + snId);
                        getSharedPreferences("deviceInfo", MODE_PRIVATE).edit().putString("sbId", snId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(snId)) {
                    snId = Build.SERIAL;
                }
                terminalNoTv.setText(snId);
                handler.sendEmptyMessage(ConstantMsg.FIND_CARD_MSG_WHAT);
                break;
            case 1: //无效卡
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText("无效卡");
                welcomeLl.setVisibility(View.GONE);
                handler.sendEmptyMessageDelayed(ConstantMsg.SHOW_WELCOME_MSG_WHAT, 2000);
                break;
            case 2: //无补登订单
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText("未查询到待补登充值订单");
                welcomeLl.setVisibility(View.GONE);
                handler.sendEmptyMessageDelayed(ConstantMsg.SHOW_WELCOME_MSG_WHAT, 6000);
                break;
            case 3: //补登充值中
                resultRl.setVisibility(View.VISIBLE);
                warnmingLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.GONE);
                showLoading("正在进行补充值", "请勿移动卡片");
                break;
            case 4: //补登结果
                image.clearAnimation();
                resultRl.setVisibility(View.VISIBLE);
                warnmingLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.GONE);
                if (rechargeResult) {
                    image.setImageResource(R.drawable.icon_budnegchengong);
                    tipsMsgTv.setText("充值成功！  " + "余额：" + ByteUtil.toAmountString(ByteUtil.pasInt(cardInfo.getBalance()) / 100.0f));
                    tipsInfoTv.setText("请移除卡片");
                    handler.sendEmptyMessageDelayed(ConstantMsg.SHOW_WELCOME_MSG_WHAT, 3000);
                } else {
                    image.setImageResource(R.drawable.icon_budnegshibai);
                    tipsMsgTv.setText("错误代码：" + erroeCode);
                    tipsInfoTv.setText("充值失败 请贴卡重试！");
                    handler.sendEmptyMessageDelayed(ConstantMsg.SHOW_WELCOME_MSG_WHAT, 3000);
                }
                break;
            case 5: //网络请求错误
                if (cardInfo == null) {
                    resultRl.setVisibility(View.GONE);
                    warnmingTv.setText(errorMsg);
                    warnmingLl.setVisibility(View.VISIBLE);
                    welcomeLl.setVisibility(View.GONE);
                } else {
                    resultRl.setVisibility(View.GONE);
                    warnmingLl.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(errorMsg)) {
                        errorMsg = "未知错误";
                    }
                    warnmingTv.setText(errorMsg);
                    welcomeLl.setVisibility(View.GONE);
                }
                handler.sendEmptyMessageDelayed(ConstantMsg.SHOW_WELCOME_MSG_WHAT, 3000);
                break;
            case ConstantMsg.SHOW_CARD_MSG_WHAT: //显示卡号，余额
                welcomeLl.setVisibility(View.GONE);
                cardInfoLl.setVisibility(View.VISIBLE);
                tvCardNo.setText(cardInfo.getCardNo());
                if (cardInfo.getBalance() == null) {
                    tvBalance.setText("0");
                } else {
                    tvBalance.setText(ByteUtil.toAmountString(ByteUtil.pasInt(cardInfo.getBalance()) / 100.0f));
                }
                break;
            case ConstantMsg.NOT_USE_MSG_WHAT:
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_no_permitted_card));
                welcomeLl.setVisibility(View.GONE);
                handler.sendEmptyMessageDelayed(ConstantMsg.SHOW_WELCOME_MSG_WHAT, 2000);
                break;
            case ConstantMsg.INIT_NETWORK:
                welcomeLl.setVisibility(View.GONE);
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText(getString(R.string.str_init_network));
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

    private class Mhandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String cityCode = "3149";
            switch (msg.what) {
                case ConstantMsg.FIND_CARD_MSG_WHAT:
                    removeMessages(ConstantMsg.FIND_CARD_MSG_WHAT);
                    findCard();
                    break;
                case ConstantMsg.SHOW_WELCOME_MSG_WHAT:
                    changeViewByType(0);
                    break;
                case HttpBusiness.SIGN_IN_SUCEESS_CODE:
                    hasSignIn = true;
                    break;
                case HttpBusiness.SIGN_IN_EROOR_CODE:
                    hasSignIn = false;
                    break;
                case HttpBusiness.ERROR_CODE:
                    errorMsg = (String) msg.obj;
                    changeViewByType(5);
                    break;
                case HttpBusiness.QUERY_ORDER_EROOR_CODE:
                    changeViewByType(2);
                    break;
                case HttpBusiness.CONFIRM_RECHARGE_MONTH_EROOR_CODE:
                case HttpBusiness.START_RECHARGECARD_EROOR_CODE:
                    erroeCode = (String) msg.obj;
                    rechargeResult = false;
                    changeViewByType(4);
                    break;
                case HttpBusiness.CONFIRM_RECHARGE_EROOR_CODE:
                    rechargeResult = true;
                    changeViewByType(4);
                    break;
                case HttpBusiness.QUERY_ORDER_SUCEESS_CODE:
                    messageQueryRes = (MessageQueryRes) msg.obj;
                    changeViewByType(3);
                    reloadAmount = messageQueryRes.getMoney();
                    mOutTradeNo = messageQueryRes.getOutTradeNo();
                    topInit(messageQueryRes.getTradetype(), messageQueryRes.getMoney());
                    break;
                case HttpBusiness.START_RECHARGECARD_SUCEESS_CODE:
                    MessageApplyWriteRes messageApplyWriteRes = (MessageApplyWriteRes) msg.obj;
//                    topRecharge(messageApplyWriteRes.getMAC2(), messageApplyWriteRes.getMessageDateTime());
                    topRecharge(messageApplyWriteRes.getMAC2(), HttpBusiness.getDealStamp());
                    break;
                case HttpBusiness.CONFIRM_RECHARGE_MONTH_SUCEESS_CODE:
                    MonthTicketModifyRes monthTicket = (MonthTicketModifyRes) msg.obj;
//                    topRecharge(monthTicket.getMAC2(), "");
                    break;
                case HttpBusiness.CONFIRM_RECHARGE_SUCEESS_CODE:
                    rechargeResult = true;
                    Log.e("MAIN", "2062成功");
                    changeViewByType(4);
                    break;
                case HttpBusiness.HEART_BEAT_SUCEESS_CODE:
                    UpdateUtils.upVersion((Rda) msg.obj);
                    break;
                case ConstantMsg.SHOW_FAILDREADCARD_MSG_WHAT:
                    changeViewByType(1);
                    break;
                case ConstantMsg.SHOW_FAILDRECHARGE_MSG_WHAT:
                    rechargeResult = false;
                    changeViewByType(4);
                    break;
                case ConstantMsg.SHOW_CARD_MSG_WHAT:
                    cardInfo = (CardInfo) msg.obj;
                    changeViewByType(ConstantMsg.SHOW_CARD_MSG_WHAT);
                    break;
                case ConstantMsg.NOT_USE_MSG_WHAT:
                    changeViewByType(ConstantMsg.NOT_USE_MSG_WHAT);
                    break;
                case ConstantMsg.UPDATE_TIME_MSG_WHAT:
                    setWeekText();
                    break;
                case ConstantMsg.INIT_NETWORK:
                    netChecker();
                    break;
                case ConstantMsg.UNRECOGNIZE_CARD:
                    ToastUtils.showToast(getString(R.string.str_card_not_distinguish), MainActivity.this);
                    break;
                default:
                    break;
            }
        }
    }

    /* 开始PhoneState听众 */
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
