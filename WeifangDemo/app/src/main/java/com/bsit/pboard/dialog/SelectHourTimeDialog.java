package com.bsit.pboard.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsit.pboard.R;
import com.bsit.pboard.calendarview.DateBean;
import com.bsit.pboard.calendarview.MonthView;
import com.bsit.pboard.calendarview.listener.OnMonthItemClickListener;
import com.bsit.pboard.calendarview.utils.CalendarUtil;
import com.bsit.wheelview.TosAdapterView;
import com.bsit.wheelview.WheelView;
import com.bsit.wheelview.WheelViewCommonAdapter;
import java.util.ArrayList;
import java.util.List;

public class SelectHourTimeDialog extends BasePopupWindow {

    WheelView wheelView1;
    WheelView wheelView2;
    WheelView wheelView3;
    TextView dataTv;
    TextView timeTv;
    MonthView calendar;
    LinearLayout timeLayout;
    LinearLayout calendarLayout;

    private List<String> hourList;
    private List<String> minuteList;
    private List<String> secondList;
    private int[] times;
    protected onSubmitListener onSubmit;
    private WheelViewCommonAdapter wheelViewCommonAdapter1;
    private WheelViewCommonAdapter wheelViewCommonAdapter2;
    private WheelViewCommonAdapter wheelViewCommonAdapter3;
    private Context context;

    public SelectHourTimeDialog(Context context, int[] times, onSubmitListener onSubmit) {
        super(context);
        this.context = context;
        this.onSubmit = onSubmit;
        this.times = times;
        hourList = getHour();
        minuteList = getMinute();
        secondList = getMinute();
        initViewData();
    }

    private void initViewData() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(
                R.layout.popup_select_time_dialog, null);
        view.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        wheelView1 = (WheelView) view.findViewById(R.id.dialog_select_time_wheel1);
        wheelView2 = (WheelView) view.findViewById(R.id.dialog_select_time_wheel2);
        wheelView3 = (WheelView) view.findViewById(R.id.dialog_select_time_wheel3);
        dataTv = (TextView) view.findViewById(R.id.data_tv);
        timeTv = (TextView) view.findViewById(R.id.time_tv);
        calendar = (MonthView)view.findViewById(R.id.calendar);
        timeLayout = (LinearLayout)view.findViewById(R.id.time_layout);
        calendarLayout = (LinearLayout)view.findViewById(R.id.calendar_layout);

        view.findViewById(R.id.dialog_select_time_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit.onSubmit(times[0] + "-" + String.format("%02d", times[1]) + "-" + String.format("%02d", times[2]) + " " +
                        String.format("%02d", times[3]) + ":" + String.format("%02d", times[4]) + ":" + String.format("%02d", times[5]) );
                dismiss();
            }
        });

        view.findViewById(R.id.data_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showData();
            }
        });

        view.findViewById(R.id.time_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime();
            }
        });

        wheelViewCommonAdapter1 = new WheelViewCommonAdapter(
                context, hourList);
        wheelView1.setAdapter(wheelViewCommonAdapter1);
        String hour = String.format("%02d", times[3]);
        wheelView1.setSelection(hourList.indexOf(hour) == -1 ? 0 : hourList.indexOf(hour));
        wheelViewCommonAdapter1.setSelectPosition(hourList.indexOf(hour) == -1 ? 0 : hourList.indexOf(hour));
        wheelView1.setOnItemSelectedListener(new TosAdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(TosAdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                wheelViewCommonAdapter1.setSelectPosition(position);
                times[3] = Integer.parseInt(hourList.get(position));
                setTv();
            }

            @Override
            public void onNothingSelected(TosAdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        wheelViewCommonAdapter2 = new WheelViewCommonAdapter(
                context, minuteList);
        wheelView2.setAdapter(wheelViewCommonAdapter2);
        String minute = String.valueOf(times[1]);

        wheelView2.setSelection(minuteList.indexOf(minute) == -1 ? 0 : minuteList.indexOf(minute));
        wheelViewCommonAdapter2.setSelectPosition(minuteList.indexOf(minute) == -1 ? 0 : minuteList.indexOf(minute));

        wheelView2.setOnItemSelectedListener(new TosAdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(TosAdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                wheelViewCommonAdapter2.setSelectPosition(position);
                times[4] = Integer.parseInt(minuteList.get(position));
                setTv();
            }

            @Override
            public void onNothingSelected(TosAdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        wheelViewCommonAdapter3 = new WheelViewCommonAdapter(
                context, secondList);
        wheelView3.setAdapter(wheelViewCommonAdapter3);
        String second = String.valueOf(times[2]);

        wheelView3.setSelection(secondList.indexOf(second) == -1 ? 0 : secondList.indexOf(second));
        wheelViewCommonAdapter3.setSelectPosition(secondList.indexOf(minute) == -1 ? 0 : secondList.indexOf(minute));

        wheelView3.setOnItemSelectedListener(new TosAdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(TosAdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                wheelViewCommonAdapter3.setSelectPosition(position);
                times[5] = Integer.parseInt(secondList.get(position));
                setTv();
            }

            @Override
            public void onNothingSelected(TosAdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        calendar.setDateList(CalendarUtil.getMonthDate(times[0], times[1]));
        calendar.setOnItemClickListener(new OnMonthItemClickListener() {
            @Override
            public void onMonthItemClick(View view, DateBean date, int position) {
                times[0] = date.getSolar()[0];
                times[1] = date.getSolar()[1];
                times[2] = date.getSolar()[2];
                setTv();
                showTime();
            }
        });
        calendar.setFillLister(new MonthView.FillLister() {
            @Override
            public void fillLeft() {
                if (times[1] == 1) {
                    times[1] = 12;
                    times[0] = times[0] - 1;
                } else {
                    times[1] = times[1] - 1;
                }
                calendar.setDateList(CalendarUtil.getMonthDate(times[0], times[1]));
                setTv();
            }

            @Override
            public void fillRight() {
                if (times[1] == 12) {
                    times[1] = 1;
                    times[0] = times[0] + 1;
                } else {
                    times[1] = times[1] + 1;
                }
                calendar.setDateList(CalendarUtil.getMonthDate(times[0], times[1]));
                setTv();
            }
        });
        setTv();
        setViw();
    }

    private void setTv() {
        dataTv.setText(times[0] + "年" + times[1] + "月" + times[2] + "日");
        timeTv.setText(String.format("%02d", times[3]) + ":" + String.format("%02d", times[4]) + ":"
        + String.format("%02d", times[5]));
    }


    /**
     * 小时数
     *
     * @return
     */
    public static List<String> getHour() {
        List<String> hour = new ArrayList();
        for (int j = 0; j <= 23; j++) {
            hour.add(j < 10 ? "0" + j : "" + j);
        }
        return hour;
    }

    /**
     * 分钟
     *
     * @return
     */
    public static List<String> getMinute() {
        List<String> m = new ArrayList();
        for (int i = 0; i <= 59 ; i++) {
            m.add(i < 10 ? "0" + i : "" + i);
        }
        return m;
    }


    private void showData() {
        timeLayout.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.VISIBLE);
        dataTv.setBackgroundResource(R.drawable.line_input_bg);
        dataTv.setTextColor(context.getResources().getColor(R.color.font_normal));
        timeTv.setBackgroundResource(R.color.white);
        timeTv.setTextColor(context.getResources().getColor(R.color.font_gray));
    }

    private void showTime() {
        timeLayout.setVisibility(View.VISIBLE);
        calendarLayout.setVisibility(View.GONE);
        dataTv.setBackgroundResource(R.color.white);
        dataTv.setTextColor(context.getResources().getColor(R.color.font_gray));
        timeTv.setBackgroundResource(R.drawable.line_input_bg);
        timeTv.setTextColor(context.getResources().getColor(R.color.font_normal));
    }
}
