package com.bsit.pboard;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.bsit.pboard.R;
import com.bsit.pboard.dialog.BasePopupWindow;
import com.bsit.pboard.dialog.SelectHourTimeDialog;
import com.bsit.pboard.view.SwitchView;
import com.bsit.pboard.calendarview.utils.SolarUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeActivity extends Activity {

    private SwitchView dateTimeAutoIv;
    private SwitchView dateTime24hourIv;
    private TextView dateTimeNameTv;
    private TextView dateTimeTv;

    private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";
    private SelectHourTimeDialog startTimePop;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setText();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);
        dateTimeAutoIv = (SwitchView) findViewById(R.id.date_time_auto_iv);
        dateTime24hourIv = (SwitchView) findViewById(R.id.zdate_time_24hour_iv);
        dateTimeTv = (TextView) findViewById(R.id.date_time_tv);
        dateTimeNameTv = (TextView) findViewById(R.id.date_time_name_tv);
        setLister();
        setEnables();
        setText();
    }

    private void setText(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dff = null;
        if(is24Hour()){
            dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        }else{
            dff = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        }
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String formatted = dff.format(now.getTime());
        dateTimeTv.setText(formatted);
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    private void setEnables(){
        boolean autoTimeEnabled = getAutoState(Settings.Global.AUTO_TIME);
        dateTimeAutoIv.setOpened(autoTimeEnabled);
        dateTime24hourIv.setOpened(is24Hour());
        if(!autoTimeEnabled){
            dateTimeTv.setTextColor(getResources().getColor(R.color.fontColor));
            dateTimeNameTv.setTextColor(getResources().getColor(R.color.fontColor));
        }else{
            dateTimeTv.setTextColor(getResources().getColor(R.color.grayfontColor));
            dateTimeNameTv.setTextColor(getResources().getColor(R.color.grayfontColor));
        }
    }

    private boolean getAutoState(String name) {
        try {
            return Settings.Global.getInt(getContentResolver(), name) > 0;
        } catch (Settings.SettingNotFoundException snfe) {
            return false;
        }
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(this);
    }

    private void set24Hour(boolean is24Hour) {
        Settings.System.putString(getContentResolver(),
                Settings.System.TIME_12_24,
                is24Hour? HOURS_24 : HOURS_12);
    }

    private void timeUpdated() {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        sendBroadcast(timeChanged);
        setText();
    }

    public void setLister(){
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.date_time_set_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getAutoState(Settings.Global.AUTO_TIME)){
                    return;
                }

                if (startTimePop == null) {
                    startTimePop = new SelectHourTimeDialog(DateTimeActivity.this, SolarUtil.getCurrentDate(),new BasePopupWindow.onSubmitListener() {
                        @Override
                        public void onSubmit(String text) {
                            dateTimeTv.setText(text);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try{
                                Date date = sdf.parse(text);
                                long when = date.getTime();
                                ((AlarmManager) DateTimeActivity.this.getSystemService(Context.ALARM_SERVICE)).setTime(when);
                                timeUpdated();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
                startTimePop.showAtLocation(
                        findViewById(R.id.main_layout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

            }
        });
        dateTimeAutoIv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                if(!isNetworkAvailable(DateTimeActivity.this)){
                    Toast.makeText(DateTimeActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
                Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, 1);
                dateTimeAutoIv.setOpened(true);
                dateTimeTv.setTextColor(getResources().getColor(R.color.grayfontColor));
                dateTimeNameTv.setTextColor(getResources().getColor(R.color.grayfontColor));
                setText();
            }

            @Override
            public void toggleToOff(View view) {
                Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);
                dateTimeAutoIv.setOpened(false);
                dateTimeTv.setTextColor(getResources().getColor(R.color.fontColor));
                dateTimeNameTv.setTextColor(getResources().getColor(R.color.fontColor));
            }
        });
        dateTime24hourIv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                set24Hour(true);
                dateTime24hourIv.setOpened(true);
                setText();
            }

            @Override
            public void toggleToOff(View view) {
                set24Hour(false);
                dateTime24hourIv.setOpened(false);
                setText();
            }
        });
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

}
