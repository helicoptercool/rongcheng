package com.bsit.pboard.business;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bsit.pboard.model.BackInfoObject;
import com.bsit.pboard.model.BaseObject;
import com.bsit.pboard.constant.Constants;
import com.bsit.pboard.model.HeartBeatReq;
import com.bsit.pboard.model.HeartBeatRsp;
import com.bsit.pboard.model.MessageApplyWriteReq;
import com.bsit.pboard.model.MessageApplyWriteRes;
import com.bsit.pboard.model.MessageConfirmReq;
import com.bsit.pboard.model.MessageQueryReq;
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.model.MonthTicketModifyReq;
import com.bsit.pboard.model.MonthTicketModifyRes;
import com.bsit.pboard.model.YearCheckApplyReq;
import com.bsit.pboard.model.YearCheckApplyRsp;
import com.bsit.pboard.model.YearCheckNoticeReq;
import com.bsit.pboard.model.YearCheckQueryReq;
import com.bsit.pboard.model.YearCheckQueryRsp;
import com.bsit.pboard.utils.EncryptUtils;
import com.bsit.pboard.utils.MacUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guozheng.urlhttputils.urlhttp.CallBackUtil;
import com.guozheng.urlhttputils.urlhttp.UrlHttpUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class HttpBusiness {
    private static final String TAG = "HttpBusiness";
    private static HttpBusiness httpBusiness;
    private static Context context;
    private static Gson gson = new Gson();
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    private static String dealStamp = "";

    public final static int ERROR_CODE = -1;
    public final static int QUERY_ORDER_SUCEESS_CODE = 100;
    public final static int QUERY_ORDER_EROOR_CODE = -100;
    public final static int START_RECHARGECARD_SUCEESS_CODE = 101;
    public final static int START_RECHARGECARD_EROOR_CODE = -101;
    public final static int CONFIRM_RECHARGE_SUCEESS_CODE = 102;
    public final static int CONFIRM_RECHARGE_EROOR_CODE = -102;
    public final static int HEART_BEAT_REQUEST = -103;
    public final static int HEART_BEAT_SUCEESS_CODE = 103;
    public final static int CONFIRM_RECHARGE_MONTH_SUCEESS_CODE = 104;
    public final static int CONFIRM_RECHARGE_MONTH_EROOR_CODE = -104;
    public final static int SIGN_IN_SUCEESS_CODE = 105;
    public final static int SIGN_IN_EROOR_CODE = -105;
    public final static int YEAR_CHECK_QUERY_SUCCESS = 106;
    public final static int YEAR_CHECK_QUERY_FAILURE = -106;
    public final static int YEAR_CHECK_APPLY_SUCCESS = 107;
    public final static int YEAR_CHECK_APPLY_FAILURE = -107;
    public final static int YEAR_CHECK_NOTICE_SUCCESS = 108;
    public final static int YEAR_CHECK_NOTICE_FAILURE = -108;

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
     *
     * @param messageQueryReq
     * @param handler
     */
    public static void queryOrder(MessageQueryReq messageQueryReq, final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("termId", messageQueryReq.getTermId());
        paramsMap.put("messageDateTime", messageQueryReq.getMessageDateTime());
        paramsMap.put("cardId", messageQueryReq.getCardId());
        paramsMap.put("balance", messageQueryReq.getSrcBal());


        UrlHttpUtil.post(Constants.URL_QUERY_ORDER, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Log.i(TAG, "query order error = " + errorMessage);
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Log.i(TAG, "query order = " + response);
                Message msg = handler.obtainMessage();
                BackInfoObject<MessageQueryRes> backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject<MessageQueryRes>>() {
                }.getType());
                if (backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("00000")) {
                    msg.what = QUERY_ORDER_SUCEESS_CODE;
                    msg.obj = backInfoObject.getObj();
                } else {
                    msg.what = QUERY_ORDER_EROOR_CODE;
                    msg.obj = backInfoObject.getMessage();
                }
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 补登圈存
     *
     * @param messageApplyWriteReq
     * @param handler
     */
    public static void rechargeCard(MessageApplyWriteReq messageApplyWriteReq, final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("termId", messageApplyWriteReq.getTermId());
        paramsMap.put("cardId", messageApplyWriteReq.getCardId());
        paramsMap.put("cardType", messageApplyWriteReq.getCardType());
        paramsMap.put("tradetype", messageApplyWriteReq.getTradetype());
        paramsMap.put("outTradeNo", messageApplyWriteReq.getOutTradeNo());
        paramsMap.put("rndnumber", messageApplyWriteReq.getRndnumber());
        paramsMap.put("cardTradeNo", messageApplyWriteReq.getCardTradeNo());
        paramsMap.put("cardBalance", messageApplyWriteReq.getCardBalance());
        paramsMap.put("tradeMoney", messageApplyWriteReq.getTradeMoney());
        paramsMap.put("mac1", messageApplyWriteReq.getMac1());
        paramsMap.put("data0015", messageApplyWriteReq.getData0015());
        paramsMap.put("base", messageApplyWriteReq.getBase());
        paramsMap.put("messageDateTime", messageApplyWriteReq.getMessageDateTime());

        UrlHttpUtil.post(Constants.URL_RECHARGE_APPLY, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Log.i(TAG, "rechargeCard failure = " + errorMessage);
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Log.i(TAG, "rechargeCard response = " + response);
                Message msg = handler.obtainMessage();
                BackInfoObject<MessageApplyWriteRes> backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject<MessageApplyWriteRes>>() {
                }.getType());
                if (backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("00000")) {
                    msg.what = START_RECHARGECARD_SUCEESS_CODE;
                    msg.obj = backInfoObject.getObj();
                } else {
                    msg.what = START_RECHARGECARD_EROOR_CODE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * CPU卡补登月票有效期修改（月票才需）
     *
     * @param handler
     */
    public static void monthTicketModify(MonthTicketModifyReq monthTicketReq, final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();

        paramsMap.put("termId", monthTicketReq.getTermId());
        paramsMap.put("cardId", monthTicketReq.getCardId());
        paramsMap.put("outTradeNo", monthTicketReq.getOutTradeNo());
        paramsMap.put("cardType", monthTicketReq.getCardType());
        paramsMap.put("data0015", monthTicketReq.getData0015());
        paramsMap.put("ats", monthTicketReq.getAts());
        paramsMap.put("termTradeNo", monthTicketReq.getTermTradeNo());
        paramsMap.put("messageDateTime", monthTicketReq.getMessageDateTime());

        UrlHttpUtil.post(Constants.URL_MONTY_TICKET_UPDATE, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Log.i(TAG, "error code = " + code + ", errmsg = " + errorMessage);
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Log.i(TAG, "monthTicketModify = " + response);
                Message msg = handler.obtainMessage();
                BackInfoObject<MonthTicketModifyRes> backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject<MonthTicketModifyRes>>() {
                }.getType());
                if (backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("00000")) {
                    msg.what = CONFIRM_RECHARGE_MONTH_SUCEESS_CODE;
                    msg.obj = backInfoObject.getObj();
                } else {
                    msg.what = CONFIRM_RECHARGE_MONTH_EROOR_CODE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }


    /**
     * 补登确认
     *
     * @param messageConfirmReq
     * @param handler
     */
    public static void confirmRecharge(MessageConfirmReq messageConfirmReq, final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("termId", messageConfirmReq.getTermId());
        paramsMap.put("cardId", messageConfirmReq.getCardId());
        paramsMap.put("outTradeNo", messageConfirmReq.getOutTradeNo());
        paramsMap.put("cardType", messageConfirmReq.getCardType());
        paramsMap.put("status", messageConfirmReq.getStatus());
        paramsMap.put("tac", messageConfirmReq.getTac());
        paramsMap.put("messageDateTime", messageConfirmReq.getMessageDateTime());
        UrlHttpUtil.post(Constants.URL_RECHARGE_CONFIRM, paramsMap, new CallBackUtil.CallBackString() {
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
                BackInfoObject backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject>() {
                }.getType());
                if (backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("00000")) {
                    msg.what = CONFIRM_RECHARGE_SUCEESS_CODE;
                    msg.obj = backInfoObject.getObj();
                } else {
                    msg.what = CONFIRM_RECHARGE_EROOR_CODE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 年检查询
     *
     * @param req
     * @param handler
     */
    public static void queryYearCheck(YearCheckQueryReq req, final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("termId", req.getTermId());
        paramsMap.put("cardId", req.getCardId());
        UrlHttpUtil.post(Constants.URL_QUERY_YEAR_CHECK, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Log.i(TAG, "queryYearCheck = " + response);
                Message msg = handler.obtainMessage();
                BackInfoObject<YearCheckQueryRsp> backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject<YearCheckQueryRsp>>() {
                }.getType());
                if (backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("00000")) {
                    msg.what = YEAR_CHECK_QUERY_SUCCESS;
                    msg.obj = backInfoObject.getObj();
                } else {
                    msg.what = YEAR_CHECK_QUERY_FAILURE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 年检请求
     *
     * @param req
     * @param handler
     */
    public static void applyYearCheck(YearCheckApplyReq req, final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("termId", req.getTermId());
        paramsMap.put("cardId", req.getCardId());
        paramsMap.put("ats", req.getAts());
        paramsMap.put("random", req.getRandom());
        UrlHttpUtil.post(Constants.URL_APPLY_YEAR_CHECK, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Log.i(TAG, "applyYearCheck = " + response);
                Message msg = handler.obtainMessage();
                BackInfoObject<YearCheckApplyRsp> backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject<YearCheckApplyRsp>>() {
                }.getType());
                if (backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("00000")) {
                    msg.what = YEAR_CHECK_APPLY_SUCCESS;
                    msg.obj = backInfoObject.getObj();
                } else {
                    msg.what = YEAR_CHECK_APPLY_FAILURE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 年检通知
     *
     * @param req
     * @param handler
     */
    public static void noticeYearCheck(YearCheckNoticeReq req, final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("termId", req.getTermId());
        paramsMap.put("cardId", req.getCardId());
        paramsMap.put("writecCardResult", req.getWritecCardResult());
        UrlHttpUtil.post(Constants.URL_NOTICE_YEAR_CHECK, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Log.i(TAG, "noticeYearCheck = " + response);
                Message msg = handler.obtainMessage();
                BackInfoObject backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject>() {
                }.getType());
                if (backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("00000")) {
                    msg.what = YEAR_CHECK_NOTICE_SUCCESS;
                    msg.obj = backInfoObject.getObj();
                } else {
                    msg.what = YEAR_CHECK_NOTICE_FAILURE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }


    public static void heartBeat(HeartBeatReq heartBeatReq, final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("deviceId", heartBeatReq.getDeviceId()); //L29 设备唯一编号（必须代表机具的唯一性） 前5位位设备类型 例如：P209A1D002D343647093338363435
        paramsMap.put("supplierNo", heartBeatReq.getSupplierNo()); //L8 供应商编码
        paramsMap.put("dateTime", heartBeatReq.getDateTime()); //L14 心跳时间
        paramsMap.put("merchantNo", heartBeatReq.getMerchantNo()); //L8 公司编码
        paramsMap.put("posId", heartBeatReq.getPosId()); //L12 现场环境中设备被分配的POS ID
        paramsMap.put("termNo", heartBeatReq.getTermNo()); //L12 现场环境中设备被分配的终端编码
        paramsMap.put("samId", heartBeatReq.getSamId()); //L16 现场环境中设备被分配的psamId
        paramsMap.put("binVer", heartBeatReq.getBinVer()); //M32 嵌入式软件版本号
        paramsMap.put("binDate", heartBeatReq.getBinDate()); //L14 编译时间
        paramsMap.put("whiteVer", heartBeatReq.getWhiteVer()); //L20 本地白名单版本号
        paramsMap.put("regionCode", heartBeatReq.getRegionCode()); //L4 设备所在城市编码
        paramsMap.put("dataCounts", heartBeatReq.getDataCounts()); //M6 设备未上传交易记录数
        paramsMap.put("latestTradeTime", heartBeatReq.getLatestTradeTime()); //L14 最近一次交易时间
        paramsMap.put("latestBootTime", heartBeatReq.getLatestBootTime());
        String dataSign = getDataSign(heartBeatReq.getDeviceId() + heartBeatReq.getSupplierNo() + heartBeatReq.getDateTime() +
                heartBeatReq.getMerchantNo() + heartBeatReq.getTermNo());
        paramsMap.put("dataSign", dataSign);
        UrlHttpUtil.post(Constants.URL_HEART_BEAT, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                // TODO: 18-11-2 remeber to delete
                Log.i(TAG, "heart beat failure = " + errorMessage);
            }

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "heart beat success = " + response);
                Message msg = handler.obtainMessage();
                BaseObject<HeartBeatRsp> backInfoObject = gson.fromJson(response, new TypeToken<BaseObject<HeartBeatRsp>>() {
                }.getType());
                if (backInfoObject.getCode() != null && backInfoObject.getCode().equals("00000")) {
                    msg.what = HEART_BEAT_SUCEESS_CODE;
                    msg.obj = backInfoObject.getContent();
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private static String getDataSign(String sourceData){
        String macKay = "82040620FEFAC4511FC65000ADAB0F77";
        String dataSign = EncryptUtils.calculateMac(sourceData, macKay);
        return dataSign;
    }

    /**
     * 心跳请求
     *
     * @param equId
     * @param cityCode
     * @param binVer
     * @param hardWorkVer
     * @param handler
     */
    /*public static void heartBeat(String equId, final String cityCode, String binVer, String hardWorkVer, final Handler handler) {
        if (!isNetworkAvailable(context)) {
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
        UrlHttpUtil.post(Constants.URL_HEART_BEAT, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
            }

            @Override
            public void onResponse(String response) {
                Log.e("TAG", response);
                Message msg = handler.obtainMessage();
                BaseObject<Rda> backInfoObject = gson.fromJson(response, new TypeToken<BaseObject<Rda>>() {
                }.getType());
                if (backInfoObject.getCode() != null && backInfoObject.getCode().equals("00000")) {
                    msg.what = HEART_BEAT_SUCEESS_CODE;
                    msg.obj = backInfoObject.getContent();
                    handler.sendMessage(msg);
                }
            }
        });
    }*/


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

    /**
     * 签到获取秘钥：每次开机后，网络初始化成功后发起
     *
     * @param handler
     */
    public static void signIn(final Handler handler) {
        if (!isNetworkAvailable(context)) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE;
            msg.obj = "设备无网络";
            handler.sendMessage(msg);
            return;
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("termId", MacUtils.getMac());
        paramsMap.put("messageDateTime", getTime());


        UrlHttpUtil.post(Constants.URL_SIGN_IN, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {
                Log.i(TAG, "sign failure === eeeeeeee");
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {
                Log.i("*******", "sign ========== " + response);
                Message msg = handler.obtainMessage();
                BackInfoObject backInfoObject = gson.fromJson(response, new TypeToken<BackInfoObject>() {
                }.getType());
                if (backInfoObject.getStatus() != null && backInfoObject.getStatus().equals("00000")) {
                    msg.what = SIGN_IN_SUCEESS_CODE;
                    msg.obj = backInfoObject.getObj();
                } else {
                    msg.what = SIGN_IN_EROOR_CODE;
                    msg.obj = backInfoObject.getStatus();
                }
                handler.sendMessage(msg);
            }
        });
    }

    public static String getTime() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dff = null;
        dff = new SimpleDateFormat("yyyyMMddHHmmss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String formatted = dff.format(now.getTime());
        dealStamp = formatted;
        return formatted;
    }

    public static String getDealStamp() {
        return dealStamp;
    }
}
