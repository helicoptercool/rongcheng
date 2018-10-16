package com.bsit.pboard;

import android.app.Application;

import com.bsit.pboard.utils.CrashHandler;

public class CrashApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
