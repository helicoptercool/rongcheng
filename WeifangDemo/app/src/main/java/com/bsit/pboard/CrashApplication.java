package com.bsit.pboard;

import android.app.Application;

import com.bsit.pboard.utils.CrashHandler;
import com.bsit.pboard.utils.MacUtils;
import com.bsit.pboard.utils.ShellUtils;

public class CrashApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        MacUtils.setDiscoverableTimeout(300);
        new Thread(){
            @Override
            public void run() {
                super.run();
                ShellUtils.execCommand("/system/bin/dongle_test 1", false);
            }
        }.start();
    }
}
