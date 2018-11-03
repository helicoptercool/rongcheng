package com.bsit.pboard.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.bsit.pboard.constant.Constants;
import com.bsit.pboard.model.HeartBeatRsp;
import com.bsit.pboard.model.Rda;
import com.guozheng.urlhttputils.urlhttp.CallBackUtil;
import com.guozheng.urlhttputils.urlhttp.UrlHttpUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class UpdateUtils {

    public static String installSilently(String path) {

        String[] args = {"pm", "install", "-r", path};
        String result = "";
        // 创建一个操作系统进程并执行命令行操作
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static void upVersion(Rda rda) {
        UrlHttpUtil.downloadFile(rda.getBhPath(), new CallBackUtil.CallBackFile("/sdcard", rda.getBhVer() + ".apk") {
            @Override
            public void onFailure(int code, String errorMessage) {

            }

            @Override
            public void onResponse(File response) {
                if (response != null) {
                    installSilently(response.getPath());
                }
            }
        });
    }


    public static void upVersion(HeartBeatRsp heartBeatRsp) {
        UrlHttpUtil.downloadFile(Constants.URL_DOWN_LOAD_FILE, new CallBackUtil.CallBackFile("/sdcard", heartBeatRsp.getBinVer() + ".apk") {
            @Override
            public void onFailure(int code, String errorMessage) {

            }

            @Override
            public void onResponse(File response) {
                if (response != null) {
                    installSilently(response.getPath());
                }
            }
        });
    }



    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public static String getTime() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dff = null;
        dff = new SimpleDateFormat("yyyyMMddHHmmss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String formatted = dff.format(now.getTime());
        return formatted;
    }
}
