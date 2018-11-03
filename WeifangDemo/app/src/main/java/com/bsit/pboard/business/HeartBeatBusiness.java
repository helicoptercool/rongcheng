package com.bsit.pboard.business;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bsit.pboard.constant.Constants;
import com.bsit.pboard.model.BaseObject;
import com.bsit.pboard.model.HeartBeatRsp;
import com.bsit.pboard.utils.EncryptUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guozheng.urlhttputils.urlhttp.CallBackUtil;
import com.guozheng.urlhttputils.urlhttp.UrlHttpUtil;

import java.io.File;
import java.util.HashMap;

public class HeartBeatBusiness {
    private static HeartBeatBusiness dataBusiness;
    private static Context context;
    private static Gson gson = new Gson();

    public final static int ERROR_CODE = -1;
    public final static int HEART_BEAT_SUCEESS_CODE = 100;
    public final static int DOWN_FILE_SUCEESS_CODE = 101;
    public final static int UPLOAD_FHINFO_SUCEESS_CODE = 102;
    public final static int UPLOAD_FHAINFO_SUCEESS_CODE = 103;
    public final static int UPLOAD_QRINFO_SUCEESS_CODE = 104;


    private HeartBeatBusiness(Context context) {
        this.context = context;
    }

    public static HeartBeatBusiness getInstance(Context context) {
        if (dataBusiness == null) {
            dataBusiness = new HeartBeatBusiness(context);
        }
        return dataBusiness;
    }

    private static String getDataSign(String sourceData){
        String macKay = "82040620FEFAC4511FC65000ADAB0F77";
        String dataSign = EncryptUtils.calculateMac(sourceData, macKay);
        return dataSign;
    }

    /**
     * 心跳上传
     * @param handler
     */
    public void saveHeartBeat(final Handler handler, String deviceId, String supplierNo, String dateTime,
                                     String merchantNo, String posId, String termNo, String samId, String binVer,
                                     String binDate, String blackVer, String whiteVer, String commBlackVer,
                                     String commWhiteVer, String regionCode, String dataCounts,
                                     String latestTradeTime, String latestBootTime, String lineNo, String hardworkVer){
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("supplierNo", supplierNo);
        paramsMap.put("dateTime", dateTime);
        paramsMap.put("merchantNo", merchantNo);
        paramsMap.put("posId", posId);
        paramsMap.put("termNo", termNo);
        paramsMap.put("samId", samId);
        paramsMap.put("binVer", binVer);
        paramsMap.put("binDate", binDate);
        paramsMap.put("blackVer", blackVer);
        paramsMap.put("whiteVer", whiteVer);
        paramsMap.put("commBlackVer", commBlackVer);
        paramsMap.put("commWhiteVer", commWhiteVer);
        paramsMap.put("regionCode", regionCode);
        paramsMap.put("dataCounts", dataCounts);
        paramsMap.put("latestTradeTime", latestTradeTime);
        paramsMap.put("latestBootTime", latestBootTime);
        paramsMap.put("lineNo", lineNo);
        paramsMap.put("hardworkVer", hardworkVer);
        String dataSign = getDataSign(deviceId + supplierNo + dateTime +
                merchantNo + termNo);
        paramsMap.put("dataSign", dataSign);
//        String url =  "http://" + SpUtils.getIpPort(context) + Contants.SAVEHEARTBEAT_URL;
        String url =  Constants.URL_HEART_BEAT;
        UrlHttpUtil.post(url, paramsMap, new CallBackUtil.CallBackString() {
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
                if(!TextUtils.isEmpty(response)){
                    Log.e("SAVEHEARTBEAT RESPONSE:", response);
                    BaseObject<HeartBeatRsp> backInfoObject = gson.fromJson(response, new TypeToken<BaseObject<HeartBeatRsp>>(){}.getType());
                    if(backInfoObject.getCode() != null && backInfoObject.getCode().equals("00000")){
                        msg.what = HEART_BEAT_SUCEESS_CODE;
                        msg.obj = backInfoObject.getContent();
                    }
                    handler.sendMessage(msg);
                }
            }
        });
    }

    /**
     * 文件下载
     * @param handler
     */
    public void downFile(final Handler handler, String deviceId, String deviceSupplierNo,
                                String merchantNo, final String fileName, String path, final String tag){
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("deviceSupplierNo", deviceSupplierNo);
        paramsMap.put("merchantNo", merchantNo);
        paramsMap.put("fileName", fileName);
        String dataSign = getDataSign(deviceId + deviceSupplierNo + merchantNo + fileName);
        paramsMap.put("dataSign", dataSign);
//        String url =  "http://" + SpUtils.getIpPort(context) + Contants.DOWNFILE_URL;
        String url =  Constants.URL_DOWN_LOAD_FILE;
        UrlHttpUtil.downloadFile(url, paramsMap, new CallBackUtil.CallBackFile(path, fileName) {
            @Override
            public void onFailure(int code, String errorMessage) {
                Log.e("DOWNFILE ONFAILURE:", code + errorMessage);
                Message msg = handler.obtainMessage();
                msg.what = ERROR_CODE;
                msg.obj = errorMessage;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(File response) {
                if(response != null){
                    Log.e("DOWNFILE RESPONSE:", response.getPath());
                    Message msg = handler.obtainMessage();
                    msg.what = DOWN_FILE_SUCEESS_CODE;
                    msg.obj = tag + "|" + response.getPath() + "|" + fileName;
                    handler.sendMessage(msg);
                }
            }
        });
    }
}
