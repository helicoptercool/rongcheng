package com.bsit.pboard;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.bsit.pboard.business.HttpBusiness;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiService extends Service {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(!HttpBusiness.isNetworkAvailable(getApplicationContext())){
                Connect("采姑娘的小蘑菇", "wobuzhidao");
            }
            handler.sendEmptyMessageDelayed(0, 5000);
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                setTv(info);
            }
        }
    };

    WifiManager wifiManager;
    IntentFilter mFilter;

    public WifiService() {
    }

    private void setTv(NetworkInfo info){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String tInfo = "";
        if(wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID()) && !wifiInfo.getSSID().equals("0x")){
            tInfo = "已连接到网络:"  + wifiInfo.getSSID().replace("\"","").replace("\"","").replace("\"","").replace("\"","");
        }else{
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                tInfo = "连接已断开";
            }else {
                NetworkInfo.DetailedState state = info.getDetailedState();
                if (state == state.CONNECTING) {
                    tInfo= "连接中...";
                } else if (state == state.AUTHENTICATING) {
                    tInfo = "正在验证身份信息...";
                } else if (state == state.OBTAINING_IPADDR) {
                    tInfo = "正在获取IP地址...";
                } else if (state == state.FAILED) {
                    tInfo = "连接失败";
                }
            }
        }
        Toast.makeText(this, tInfo, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, mFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*销毁时注销广播*/
        unregisterReceiver(broadcastReceiver);
    }

    //打开wifi功能
    private boolean OpenWifi()
    {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled())
        {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    //提供一个外部接口，传入要连接的无线网
    public boolean Connect(String SSID, String Password)
    {
        if(TextUtils.isEmpty(SSID) || TextUtils.isEmpty(Password)){
            return false;
        }

        Toast.makeText(this, "开始连接:" + SSID + "&" + Password, Toast.LENGTH_SHORT).show();
        if(!this.OpenWifi())
        {
            return false;
        }
    //开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
    //状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING )
        {
            try{
                //为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            }
            catch(InterruptedException ie){
            }
        }

        WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password);
        //
        if(wifiConfig == null)
        {
            return false;
        }
        int netID = wifiManager.addNetwork(wifiConfig);
        boolean bRet = wifiManager.enableNetwork(netID, true);
        return bRet;
    }

    private WifiConfiguration CreateWifiInfo(String SSID, String Password)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + SSID + "\"";
        config.preSharedKey = "\""+Password+"\"";
        config.hiddenSSID = true;
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.status = WifiConfiguration.Status.ENABLED;
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        return config;
    }
}
