package com.bsit.pboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.bsit.pboard.adapter.WifiAdapter;
import java.util.ArrayList;
import java.util.List;

public class WifiActivity extends Activity {

    private WifiManager mWifiManager;
    private WifiAdapter wifiAdapter;
    private List<ScanResult> scanResults = new ArrayList<ScanResult>();
    private IntentFilter mFilter;
    private WifiConfiguration config;
    private EditText editText;
    public static final String WIFI_AUTH_OPEN = "";
    public static final String WIFI_AUTH_ROAM = "[ESS]";

    private ListView wifiLv;
    private TextView wifiNameTv;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> scanResultLs = mWifiManager.getScanResults();
                scanResults.clear();
                scanResults.addAll(scanResultLs);
                initAdapter();
            }else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                setTv(info);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        wifiLv = (ListView) findViewById(R.id.wifi_lv);
        wifiNameTv = (TextView) findViewById(R.id.wifi_name);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mWifiManager.disconnect();
                final ScanResult scanResult = scanResults.get(position);
                if (!isOpen(scanResult)) {//需要密码
                    editText = new EditText(WifiActivity.this);
                    new AlertDialog.Builder(WifiActivity.this).setTitle("请输入Wifi密码").setIcon(
                            android.R.drawable.ic_dialog_info).setView(
                            editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            config = createWifiInfo(scanResult.SSID, editText.getText().toString());
                            connect(config);
                        }
                    }).setNegativeButton("取消", null).show();
                    return;
                } else {
                    config = createWifiInfo(scanResult.SSID, "");
                    connect(config);
                }
            }
        });
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, mFilter);
        search();
    }

    /*设置要连接的热点的参数*/
    public WifiConfiguration createWifiInfo(String ssid, String password){
        WifiConfiguration apConfig=new WifiConfiguration();
        apConfig.SSID="\"" + ssid + "\"";
        apConfig.preSharedKey="\"" + password + "\"";
        apConfig.hiddenSSID = true;
        apConfig.status = WifiConfiguration.Status.ENABLED;
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return apConfig;
    }

    private boolean isOpen(ScanResult scanResult){
        if (scanResult.capabilities != null) {
            String capabilities = scanResult.capabilities.trim();
            if (capabilities != null && (capabilities.equals(WIFI_AUTH_OPEN) || capabilities.equals(WIFI_AUTH_ROAM))) {
                return true;
            }
        }
        return false;
    }

    private void connect(WifiConfiguration config) {
        int wcgID = mWifiManager.addNetwork(config);
        boolean flag = mWifiManager.enableNetwork(wcgID, true);
        if(flag){
            getSharedPreferences("wifi_info", MODE_PRIVATE).edit()
                    .putString("wifi_name", config.SSID.replace("\"","").replace("\"","").replace("\"","").replace("\"",""))
                    .putString("password", config.preSharedKey.replace("\"","").replace("\"","").replace("\"","").replace("\"","")).commit();
        }
        Log.e("TAG", "connect success? "+flag);
    }
    private void setTv(NetworkInfo info){
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if(wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID()) && !wifiInfo.getSSID().equals("0x")){
            wifiNameTv.setText("已连接到网络:"  + wifiInfo.getSSID().replace("\"","").replace("\"","").replace("\"","").replace("\"",""));
        }else{
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                wifiNameTv.setText("连接已断开");
            }else {
                NetworkInfo.DetailedState state = info.getDetailedState();
                if (state == state.CONNECTING) {
                    wifiNameTv.setText("连接中...");
                } else if (state == state.AUTHENTICATING) {
                    wifiNameTv.setText("正在验证身份信息...");
                } else if (state == state.OBTAINING_IPADDR) {
                    wifiNameTv.setText("正在获取IP地址...");
                } else if (state == state.FAILED) {
                    wifiNameTv.setText("连接失败");
                }
            }
        }
    }

    private void search() {
        if (!mWifiManager.isWifiEnabled()) {
            //开启wifi
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.startScan();
    }


    private void initAdapter(){
        if(wifiAdapter == null){
            wifiAdapter = new WifiAdapter(this, scanResults);
            wifiLv.setAdapter(wifiAdapter);
        }
        wifiAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*销毁时注销广播*/
        unregisterReceiver(broadcastReceiver);
    }

}
