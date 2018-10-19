package com.bsit.pboard.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MacUtils {
    public static String getWifiMac(Context ctx) {
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        if (mac == null) {
            mac = "";
        }else {
            mac = checkMac(mac);
        }
        return mac;
    }

    public static String getMac() {
        ShellUtils.CommandResult result = ShellUtils.execCommand("cat /sys/class/net/wlan0/address ", true);
        String macAddr = checkMac(result.successMsg);
        return macAddr;
    }

    private static String checkMac(String origialMac){
        return origialMac.trim().replace(":","");
    }

    /*public static String getMac() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }*/
}
