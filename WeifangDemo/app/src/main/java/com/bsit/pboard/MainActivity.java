package com.bsit.pboard;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bsit.pboard.R;
import com.bsit.pboard.business.CardBusiness;
import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.MessageApplyWriteReq;
import com.bsit.pboard.model.MessageApplyWriteRes;
import com.bsit.pboard.model.MessageConfirmReq;
import com.bsit.pboard.model.MessageQueryReq;
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.model.Rda;
import com.bsit.pboard.utils.ByteUtil;
import com.bsit.pboard.utils.QRCodeUtil;
import com.bsit.pboard.utils.ShellUtils;
import com.bsit.pboard.utils.ToastUtils;
import com.guozheng.urlhttputils.urlhttp.CallBackUtil;
import com.guozheng.urlhttputils.urlhttp.UrlHttpUtil;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends Activity{

    /**UI控件申明**/
    private TextView dateTimeTv;
    private TextView cardNoTv;
    private TextView warnmingTv;
    private TextView deviceNameTv;
    private TextView tipsMsgTv;
    private TextView tipsInfoTv;
    private TextView snTv;
    private ImageView signalIntensityIv;
    private ImageView qrIv;
    private ImageView image;
    private RelativeLayout bottomRl;
    private RelativeLayout resultRl;
    private LinearLayout warnmingLl;
    private LinearLayout welcomeLl;

    /**工具类申明**/
    private TelephonyManager Tel;
    private MyPhoneStateListener MyListener;
    private HttpBusiness httpBusiness;
    private CardBusiness cardBusiness;

    /* 开始PhoneState听众 */
    private class MyPhoneStateListener extends PhoneStateListener {
        /* 从得到的信号强度,每个tiome供应商有更新 */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int asu = signalStrength.getGsmSignalStrength();
            if (asu <= 2 || asu == 99){
                signalIntensityIv.setImageResource(R.drawable.signal_intensity0);
            } else if (asu >= 12){
                signalIntensityIv.setImageResource(R.drawable.signal_intensity4);
            } else if (asu >= 8){
                signalIntensityIv.setImageResource(R.drawable.signal_intensity3);
            }else if (asu >= 5){
                signalIntensityIv.setImageResource(R.drawable.signal_intensity2);
            } else{
                signalIntensityIv.setImageResource(R.drawable.signal_intensity1);
            }
        }
    }

    /**全局变量申明**/
    private CardInfo cardInfo;
    private MessageQueryRes messageQueryRes;
    private boolean rechargeResult;
    private String csn;
    private String deviceId = "000020171214";
    private String cityCode = "3149";
    private String reloadAmount;
    private String rechargeId;
    private String erroeCode;
    private String errorMsg;
    private SimpleDateFormat dff;

    /**Handler句柄模式**/
    private final static int FIND_CARD_MSG_WHAT = -2;
    private final static int UPDATE_TIME_MSG_WHAT = 0;
    private final static int HEART_BEAT_MSG_WHAT = 1;
    private final static int SHOW_WELCOME_MSG_WHAT = 2;
    private final static int SHOW_FAILDREADCARD_MSG_WHAT = 3;
    private final static int SHOW_FAILDRECHARGE_MSG_WHAT = 4;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FIND_CARD_MSG_WHAT:
                    removeMessages(FIND_CARD_MSG_WHAT);
                    findCard();
                    break;
                case HEART_BEAT_MSG_WHAT:
                    if(!TextUtils.isEmpty(cityCode) && !TextUtils.isEmpty(deviceId)){
                        HttpBusiness.heartBeat(deviceId, cityCode, getVerName(MainActivity.this),"", handler);
                    }
                    sendEmptyMessageDelayed(HEART_BEAT_MSG_WHAT, 600000);
                    break;
                case SHOW_WELCOME_MSG_WHAT:
                    changeViewByType(0);
                    break;
                case HttpBusiness.ERROR_CODE:
                    errorMsg = (String)msg.obj;
                    changeViewByType(5);
                    break;
                case HttpBusiness.QUERY_ORDER_EROOR_CODE:
                    changeViewByType(2);
                    break;
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
                    reloadAmount = messageQueryRes.getReloadAmount();
                    rechargeId = messageQueryRes.getRechargeId();
                    topInit();
                    break;
                case HttpBusiness.START_RECHARGECARD_SUCEESS_CODE:
                    MessageApplyWriteRes messageApplyWriteRes = (MessageApplyWriteRes) msg.obj;
                    topRecharge(messageApplyWriteRes.getMac2(), messageApplyWriteRes.getMessageDateTime());
                    break;
                case HttpBusiness.CONFIRM_RECHARGE_SUCEESS_CODE:
                    rechargeResult = true;
                    Log.e("MAIN", "2062成功");
                    changeViewByType(4);
                    break;
                case HttpBusiness.HEART_BEAT_SUCEESS_CODE:
                    upVersion((Rda)msg.obj);
                    break;
                case SHOW_FAILDREADCARD_MSG_WHAT:
                    changeViewByType(1);
                    break;
                case SHOW_FAILDRECHARGE_MSG_WHAT:
                    rechargeResult = false;
                    changeViewByType(4);
                    break;
            }
        }
    };

    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_TIME_MSG_WHAT:
                    setTimeText();
                    break;
            }
        }
    };

    /** 网络句柄对象 **/
    private Handler netHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            netChecker();
        }
    };

    /**
     * 网络检测
     */
    private void netChecker(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(!HttpBusiness.isNetworkAvailable(MainActivity.this)){
                    ShellUtils.execCommand("./etc/ppp/init.quectel-pppd &", true);
                    netHandler.sendEmptyMessageDelayed(1, 2000);
                }else{
                    netHandler.removeMessages(1);
                }
            }
        }.start();
    }

    private void findCard(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    csn = cardBusiness.findCard();
                    if(!TextUtils.isEmpty(csn)){
                        cardInfo = cardBusiness.getCardInfo();
                        cityCode = "314" + Integer.parseInt(cardInfo.getCardNo().substring(9, 11)) % 10;
                        httpBusiness.queryOrder(new MessageQueryReq(deviceId, cardInfo.getCardNo(), cityCode, csn,
                                cardInfo.getCardSType(), cardInfo.getCardMType(), cardInfo.getBalance()), handler);
                    }
                } catch (final CardBusiness.FindCardException e) {
                    cardInfo = null;
                    handler.sendEmptyMessageDelayed(FIND_CARD_MSG_WHAT, 1000);
                }catch (final CardBusiness.ReadCardException e){
                    cardInfo = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(e.getMessage(),MainActivity.this);
                        }
                    });
                    handler.sendEmptyMessageDelayed(FIND_CARD_MSG_WHAT, 1000);
                }
            }
        }.start();
    }

    private void topInit(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    cardInfo = cardBusiness.getTopInitInfo(cardInfo, reloadAmount, deviceId);
                    httpBusiness.rechargeCard(new MessageApplyWriteReq(deviceId, cityCode, cardInfo.getCardNo(), cardInfo.getCardMType(),
                            cardInfo.getCardSType(), csn, "00", reloadAmount, cardInfo.getBalance(), cardInfo.getCardSeq(), cardInfo.getKeyVer(),
                            cardInfo.getAlglnd(), cardInfo.getCardRand(), cardInfo.getQcMac(), messageQueryRes.getRechargeId()), handler);
                }catch (Exception e){
                    rechargeResult = false;
                    erroeCode = e.getMessage();
                    handler.sendEmptyMessage(SHOW_FAILDRECHARGE_MSG_WHAT);
                }
            }
        }.start();
    }


    private void topRecharge(final String mac2, final String messageDateTime){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    cardInfo = cardBusiness.getTacFormTopUp(messageDateTime + mac2, cardInfo, reloadAmount);
                    String writeFlag = TextUtils.isEmpty(cardInfo.getTac()) ? "01" : "00";
                    Log.e("MAIN", "圈存结果：" + writeFlag);
                    httpBusiness.confirmRecharge(new MessageConfirmReq(deviceId, messageDateTime, cityCode, cardInfo.getCardNo(), cardInfo.getCardSeq(),
                            reloadAmount, cardInfo.getBalance(), cardInfo.getCardMType(), cardInfo.getTac(), rechargeId, writeFlag), handler);
                }catch (Exception e){
                    rechargeResult = false;
                    erroeCode = e.getMessage();
                    Log.e("MAIN", "圈存错误：" + erroeCode);
                    handler.sendEmptyMessage(SHOW_FAILDRECHARGE_MSG_WHAT);
                }
            }
        }.start();
    }

    private String installSilently(String path) {

        // 通过命令行来安装APK
        String[] args = { "pm", "install", "-r", path };
        String result = "";
        // 创建一个操作系统进程并执行命令行操作
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    private void upVersion(Rda rda){
        UrlHttpUtil.downloadFile(rda.getBhPath(), new CallBackUtil.CallBackFile("/sdcard", rda.getBhVer() + ".apk") {
            @Override
            public void onFailure(int code, String errorMessage) {

            }

            @Override
            public void onResponse(File response) {
                if(response != null){
                    installSilently(response.getPath());
                }
            }
        });
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        httpBusiness = HttpBusiness.getInstance(this);
        cardBusiness = CardBusiness.getInstance(this);
        initView();
        HttpBusiness.setGprsEnabled(this, true);
        initSetting();
        initLister();
        setTimeText();
        handler.sendEmptyMessage(HEART_BEAT_MSG_WHAT);
    }

    private void initView(){
        dateTimeTv = (TextView) findViewById(R.id.date_time_tv);
        cardNoTv = (TextView)findViewById(R.id.card_no_tv);
        warnmingTv = (TextView)findViewById(R.id.warnming_tv);
        deviceNameTv = (TextView)findViewById(R.id.device_name_tv);
        tipsMsgTv = (TextView) findViewById(R.id.tips_loading_msg);
        tipsInfoTv = (TextView) findViewById(R.id.tips_loading_info);
        snTv = (TextView) findViewById(R.id.sn_tv);
        signalIntensityIv = (ImageView) findViewById(R.id.signal_intensity_iv);
        image = (ImageView) findViewById(R.id.image);
        qrIv = (ImageView) findViewById(R.id.qr_iv);
        bottomRl = (RelativeLayout)findViewById(R.id.bottom_rl);
        resultRl = (RelativeLayout)findViewById(R.id.result_ll);
        warnmingLl = (LinearLayout)findViewById(R.id.warnming_ll);
        welcomeLl = (LinearLayout)findViewById(R.id.welcome_ll);
        deviceNameTv.setText("嘉兴补登设备");
    }

    private void initLister(){
        MyListener = new MyPhoneStateListener();
        Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    private void setTimeText(){
        dateTimeTv.setText(dff.format(new Date()));
        timeHandler.sendEmptyMessageDelayed(UPDATE_TIME_MSG_WHAT, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        changeViewByType(0);
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(!HttpBusiness.isNetworkAvailable(MainActivity.this)){
                    netHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initSetting(){
        Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, 1);
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone("Asia/Shanghai");
    }

    private void changeViewByType(int type){
        switch (type){
            case 0: //欢迎界面
                bottomRl.setVisibility(View.GONE);
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.VISIBLE);
                String snId = getSharedPreferences("deviceInfo", MODE_PRIVATE).getString("snId", "");
                if(TextUtils.isEmpty(snId)){
                    try{
                        snId = cardBusiness.getSN();
                        getSharedPreferences("deviceInfo", MODE_PRIVATE).edit().putString("sbId", snId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(TextUtils.isEmpty(snId)){
                    snId = Build.SERIAL;
                }
                qrIv.setImageBitmap(QRCodeUtil.creatBarcode(
                        getApplicationContext(), snId, 80, 40, false));
                snTv.setText(snId);
                handler.sendEmptyMessage(FIND_CARD_MSG_WHAT);
                break;
            case 1: //无效卡
                bottomRl.setVisibility(View.GONE);
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText("无效卡");
                welcomeLl.setVisibility(View.GONE);
                handler.sendEmptyMessageDelayed(SHOW_WELCOME_MSG_WHAT, 2000);
                break;
            case 2: //无补登订单
                bottomRl.setVisibility(View.VISIBLE);
                cardNoTv.setText(cardInfo.getCardNo());
                resultRl.setVisibility(View.GONE);
                warnmingLl.setVisibility(View.VISIBLE);
                warnmingTv.setText("该卡无补登订单");
                welcomeLl.setVisibility(View.GONE);
                handler.sendEmptyMessageDelayed(SHOW_WELCOME_MSG_WHAT, 2000);
                break;
            case 3: //补登充值中
                bottomRl.setVisibility(View.VISIBLE);
                cardNoTv.setText(cardInfo.getCardNo());
                resultRl.setVisibility(View.VISIBLE);
                warnmingLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.GONE);
                showLoading("补登充值中...", "请勿移动卡片");
                break;
            case 4: //补登结果
                image.clearAnimation();
                bottomRl.setVisibility(View.VISIBLE);
                cardNoTv.setText(cardInfo.getCardNo());
                resultRl.setVisibility(View.VISIBLE);
                warnmingLl.setVisibility(View.GONE);
                welcomeLl.setVisibility(View.GONE);
                if(rechargeResult){
                    image.setImageResource(R.drawable.icon_budnegchengong);
                    tipsMsgTv.setText("充值成功！  " + "余额：" + ByteUtil.toAmountString(ByteUtil.pasInt(cardInfo.getBalance()) / 100.0f));
                    tipsInfoTv.setText("请移除卡片");
                    handler.sendEmptyMessageDelayed(SHOW_WELCOME_MSG_WHAT, 3000);
                }else{
                    image.setImageResource(R.drawable.icon_budnegshibai);
                    tipsMsgTv.setText("错误代码：" + erroeCode);
                    tipsInfoTv.setText("充值失败 请贴卡重试！");
                    handler.sendEmptyMessageDelayed(SHOW_WELCOME_MSG_WHAT, 3000);
                }
                break;
            case 5: //网络请求错误
                if(cardInfo == null){
                    bottomRl.setVisibility(View.GONE);
                    resultRl.setVisibility(View.GONE);
                    warnmingLl.setVisibility(View.GONE);
                    welcomeLl.setVisibility(View.VISIBLE);
                }else{
                    bottomRl.setVisibility(View.VISIBLE);
                    cardNoTv.setText(cardInfo.getCardNo());
                    resultRl.setVisibility(View.GONE);
                    warnmingLl.setVisibility(View.VISIBLE);
                    if(TextUtils.isEmpty(errorMsg)){
                        errorMsg = "未知错误";
                    }
                    warnmingTv.setText(errorMsg);
                    welcomeLl.setVisibility(View.GONE);
                }
                handler.sendEmptyMessageDelayed(SHOW_WELCOME_MSG_WHAT, 3000);
                break;
        }
    }

    private void showLoading(String msg, String info){
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                this, R.anim.anim_loading);
        image.setImageResource(R.drawable.animation);
        // 使用ImageView显示动画
        image.startAnimation(hyperspaceJumpAnimation);
        tipsMsgTv.setText(msg);
        tipsInfoTv.setText(info);
    }
}
