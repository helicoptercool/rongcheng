package com.bsit.pboard.adapter;


import android.content.Context;
import android.net.wifi.ScanResult;
import com.bsit.pboard.R;

import java.util.List;

public class WifiAdapter extends CommonAdapter<ScanResult> {

    Context context;
    public static final String WIFI_AUTH_OPEN = "";
    public static final String WIFI_AUTH_ROAM = "[ESS]";

    public WifiAdapter(Context context, List<ScanResult> data){
        super(context, data, R.layout.adapter_layout_wifi);
        this.context = context;
    }

    @Override
    public void convert(ViewHolder holder, ScanResult scanResult, int position) {
        if(scanResult != null){
            holder.setText(R.id.wifi_name, scanResult.SSID);
            int level = scanResult.level;
            if (level <= 0 && level >= -50) {
                if (isOpen(scanResult)) {
                    holder.setImageResource(R.id.wifi_rssi, R.drawable.ic_wifi_signal_4_light);
                }else{
                    holder.setImageResource(R.id.wifi_rssi, R.drawable.ic_wifi_lock_signal_4_light);
                }
            } else if (level < -50 && level >= -70) {
                if (isOpen(scanResult)) {
                    holder.setImageResource(R.id.wifi_rssi, R.drawable.ic_wifi_signal_3_light);
                }else{
                    holder.setImageResource(R.id.wifi_rssi, R.drawable.ic_wifi_lock_signal_3_light);
                }
            } else if (level < -70 && level >= -80) {
                if (isOpen(scanResult)) {
                    holder.setImageResource(R.id.wifi_rssi, R.drawable.ic_wifi_signal_2_light);
                }else{
                    holder.setImageResource(R.id.wifi_rssi, R.drawable.ic_wifi_lock_signal_2_light);
                }
            } else{
                if (isOpen(scanResult)) {
                    holder.setImageResource(R.id.wifi_rssi, R.drawable.ic_wifi_signal_1_light);
                }else{
                    holder.setImageResource(R.id.wifi_rssi, R.drawable.ic_wifi_lock_signal_1_light);
                }
            }
        }
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
}
