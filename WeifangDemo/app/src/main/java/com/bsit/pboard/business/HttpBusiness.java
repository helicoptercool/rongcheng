package com.bsit.pboard.business;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bsit.pboard.model.BackInfoObject;
import com.bsit.pboard.model.BaseObject;
import com.bsit.pboard.model.Constants;
import com.bsit.pboard.model.MessageApplyWriteReq;
import com.bsit.pboard.model.MessageApplyWriteRes;
import com.bsit.pboard.model.MessageConfirmReq;
import com.bsit.pboard.model.MessageConfirmRes;
import com.bsit.pboard.model.MessageQueryReq;
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.model.Rda;
import com.bsit.pboard.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guozheng.urlhttputils.urlhttp.CallBackUtil;
import com.guozheng.urlhttputils.urlhttp.UrlHttpUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HttpBusiness {
    private static HttpBusiness httpBusiness;
    private static Context context;
    private static Gson gson = new Gson();
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    public final static int ERROR_CODE = -1;
    public final static int QUERY_ORDER_SUCEESS_CODE = 100;
    public final static int QUERY_ORDER_EROOR_CODE = -100;
    public final static int START_RECHARGECARD_SUCEESS_CODE = 101;
    public final static int START_RECHARGECARD_EROOR_CODE = -101;
    public final static int CONFIRM_RECHARGE_SUCEESS_CODE = 102;
    public final static int CONFIRM_RECHARGE_EROOR_CODE = -102;
    public final static int HEART_BEAT_SUCEESS_CODE = 103;

    private HttpBusiness(Context context) {
        this.context = context;
    }

    public static HttpBusiness getInstance(Context context) {
        if (httpBusiness == null) {
            httpBusiness = new HttpBusiness(context);
        }
        return httpBusiness;
    }

    /**
     * 补登查询
     * @param messageQueryReq
     * @param handler
     */
    public static void queryOrder(MessageQueryReq messageQueryReq, final Handler handler){
        if(!isNetworkAvailable(context)){
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("deviceId", messageQueryReq.getDeviceId());
        paramsMap.put("cardId", messageQueryReq.getCardId());
        paramsMap.put("cityCode", messageQueryReq.getCityCode());
        paramsMap.put("csn", messageQueryReq.getCsn());
        paramsMap.put("cardSType", messageQueryReq.getCardSType());
        paramsMap.put("cardMType", messageQueryReq.getCardMType());
        paramsMap.put("srcBal", messageQueryReq.getSrcBal());
        UrlHttpUtil.post(Constants.QUERY_RECHARGEINFO_URL, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Message msg = handler.obtainMessage();
                BackInfoObject<MessageQueryRes> backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject<MessageQueryRes>>(){}.getType());
                if(backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("09000")){
                    msg.what = QUERY_ORDER_SUCEESS_CODE;
                    msg.obj = backInfoObject.getObj();
                }else{
                    msg.what = QUERY_ORDER_EROOR_CODE;
                    msg.obj = backInfoObject.getMessage();
                }
                handler.sendMessage(msg);
            }
        });
    }


    /**
     * 补登圈存
     * @param messageApplyWriteReq
     * @param handler
     */
    public static void rechargeCard(MessageApplyWriteReq messageApplyWriteReq,  final Handler handler){
        if(!isNetworkAvailable(context)){
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("deviceId", messageApplyWriteReq.getDeviceId());
        paramsMap.put("cardId", messageApplyWriteReq.getCardId());
        paramsMap.put("cityCode", messageApplyWriteReq.getCityCode());
        paramsMap.put("csn", messageApplyWriteReq.getCsn());
        paramsMap.put("cardSType", messageApplyWriteReq.getCardSType());
        paramsMap.put("cardMType", messageApplyWriteReq.getCardMType());
        paramsMap.put("srcBal", messageApplyWriteReq.getSrcBal());
        paramsMap.put("deposit", messageApplyWriteReq.getDeposit());
        paramsMap.put("reloadAmount", messageApplyWriteReq.getReloadAmount());
        paramsMap.put("cardSequence", messageApplyWriteReq.getCardSequence());
        paramsMap.put("rechargeId", messageApplyWriteReq.getRechargeId());
        paramsMap.put("mac1", messageApplyWriteReq.getMac1());
        paramsMap.put("cardRand", messageApplyWriteReq.getCardRand());
        paramsMap.put("alglnd", messageApplyWriteReq.getAlglnd());
        paramsMap.put("keyVer", messageApplyWriteReq.getKeyVer());
        UrlHttpUtil.post(Constants.START_RECHARGECARD_URL, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Message msg = handler.obtainMessage();
                BackInfoObject<MessageApplyWriteRes> backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject<MessageApplyWriteRes>>(){}.getType());
                if(backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("09000")){
                    msg.what = START_RECHARGECARD_SUCEESS_CODE;
                    msg.obj = backInfoObject.getObj();
                }else{
                    msg.what = START_RECHARGECARD_EROOR_CODE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }


    /**
     * 补登确认
     * @param messageConfirmReq
     * @param handler
     */
    public static void confirmRecharge(MessageConfirmReq messageConfirmReq, final Handler handler){
        if(!isNetworkAvailable(context)){
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("deviceId", messageConfirmReq.getDeviceId());
        paramsMap.put("cardId", messageConfirmReq.getCardId());
        paramsMap.put("cityCode", messageConfirmReq.getCityCode());
        paramsMap.put("messageDateTime", messageConfirmReq.getMessageDateTime());
        paramsMap.put("cardSequence", messageConfirmReq.getCardSequence());
        paramsMap.put("reloadAmount", messageConfirmReq.getReloadAmount());
        paramsMap.put("currentBalance", messageConfirmReq.getCurrentBalance());
        paramsMap.put("cardMType", messageConfirmReq.getCardMType());
        paramsMap.put("tac", messageConfirmReq.getTac());
        paramsMap.put("rechargeId", messageConfirmReq.getRechargeId());
        paramsMap.put("writeFlag", messageConfirmReq.getWriteFlag());
        UrlHttpUtil.post(Constants.CONFIRM_RECHARGEINFO_URL, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Message msg = handler.obtainMessage();
                BackInfoObject backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject>(){}.getType());
                if(backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("09000")){
                    msg.what = CONFIRM_RECHARGE_SUCEESS_CODE;
                    msg.obj = backInfoObject.getObj();
                }else{
                    msg.what = CONFIRM_RECHARGE_EROOR_CODE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 心跳请求
     * @param equId
     * @param cityCode
     * @param binVer
     * @param hardWorkVer
     * @param handler
     */
    public static void heartBeat(String equId, final String cityCode, String binVer, String hardWorkVer, final Handler handler){
        if(!isNetworkAvailable(context)){
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("equId", equId);
        paramsMap.put("cityCode", cityCode);
        paramsMap.put("dateTime", df.format(new Date()));
        paramsMap.put("binVer", binVer);
        paramsMap.put("hardworkVer", hardWorkVer);
        UrlHttpUtil.post(Constants.HEART_BEAT_URL, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
            }

            @Override
            public void onResponse(String response) {
                Log.e("TAG", response);
                Message msg = handler.obtainMessage();
                BaseObject<Rda> backInfoObject = gson.fromJson(response, new TypeToken<BaseObject<Rda>>(){}.getType());
                if(backInfoObject.getCode() != null && backInfoObject.getCode().equals("00000")){
                    msg.what = HEART_BEAT_SUCEESS_CODE;
                    msg.obj = backInfoObject.getContent();
                    handler.sendMessage(msg);
                }
            }
        });
    }




    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 100 " + "139.129.6.204");
            int status = process.waitFor();
            if (status == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    //开启/关闭GPRS
    public static void setGprsEnabled(Context context, boolean isEnable) {

        ConnectivityManager mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = mCM.getClass();
        Class[] argClasses = new Class[1];
        argClasses[0] = boolean.class;

        try {
            Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);
            method.invoke(mCM, isEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
