package com.bsit.pboard.utils;

import android.bluetooth.BluetoothAdapter;

import java.lang.reflect.Method;

public class MacUtils {

    public static String getMac() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        String macAddress = adapter.getAddress();
        String macAddr = checkMac(macAddress);
        return macAddr;
    }

    public static void setDiscoverableTimeout(int timeout) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            adapter.enable();
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, timeout);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String checkMac(String origialMac) {
        return origialMac.trim().replace(":", "");
    }
}
