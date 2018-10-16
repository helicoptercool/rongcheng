package com.bsit.pboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bsit.pboard.view.SwitchView;
import com.bsit.pboard.R;

public class SettingActivity extends Activity {

    private SwitchView switchView;
    private RelativeLayout wifiLayout;
    private RelativeLayout dateTimeLayout;
    private RelativeLayout resetLayout;
    private RelativeLayout systemLayout;
    private RelativeLayout deviceInfoLayout;
    private TextView connectWifiName;
    private WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        switchView = (SwitchView) findViewById(R.id.swich_bt);
        wifiLayout = (RelativeLayout) findViewById(R.id.wifi_setting_ll);
        dateTimeLayout = (RelativeLayout) findViewById(R.id.data_time_ll);
        resetLayout = (RelativeLayout) findViewById(R.id.reset_ll);
        systemLayout = (RelativeLayout) findViewById(R.id.system_ll);
        deviceInfoLayout = (RelativeLayout) findViewById(R.id.device_info_ll);
        connectWifiName = (TextView) findViewById(R.id.connect_wif_name_tv);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        setLister();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();
    }

    private void setView(){
        if(isWifiOpened()){
            switchView.setOpened(true);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID()) && !wifiInfo.getSSID().equals("0x")){
                Log.e("TAG", wifiInfo.getSSID());
                connectWifiName.setText(wifiInfo.getSSID().replace("\"","")
                                    .replace("\"",""));
            }else{
                connectWifiName.setVisibility(View.GONE);
            }
        }else{
            switchView.setOpened(false);
            connectWifiName.setVisibility(View.GONE);
        }
    }

    private void setLister(){
        switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                wifiManager.setWifiEnabled(true);
                onWifiOpenDoing();
                setView();
            }

            @Override
            public void toggleToOff(View view) {
                wifiManager.setWifiEnabled(false);
                onWifiCloseDoing();
                setView();
            }
        });

        wifiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
                    startActivity(new Intent(SettingActivity.this, WifiActivity.class));
                }else{
                    Toast.makeText(SettingActivity.this, "WIFI未开启", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dateTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DateTimeActivity.class));
            }
        });

        resetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, BackupResetActivity.class));
            }
        });

        systemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, SystemActivity.class));
            }
        });

        deviceInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DeviceInfoActivity.class));
            }
        });

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * wifi打开后执行某个操作
     */
    private void onWifiOpenDoing(){
        while(!isWifiOpened()){
            try{
                //为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(50);
            }
            catch(InterruptedException ie){
                Log.i("log", ie.getMessage());
            }
        }
        //在wifi打开后执行的操作都写在这下面
        Log.i("log", "wifi已经打开");
    }


    /**
     * wifi打开后执行某个操作
     */
    private void onWifiCloseDoing(){
        while(isWifiOpened()){
            try{
                //为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(50);
            }
            catch(InterruptedException ie){
                Log.i("log", ie.getMessage());
            }
        }
        //在wifi打开后执行的操作都写在这下面
        Log.i("log", "wifi已经打开");
    }



    /**
     * 判断wifi是否已经打开
     * @return  true：已打开、false:未打开
     */
    public boolean isWifiOpened(){
        int status = wifiManager.getWifiState();
        if (status == WifiManager.WIFI_STATE_ENABLED ) {
            //wifi已经打开
            return true;
        }else {
            return false;
        }
    }

}
