package com.bsit.pboard.calendarview.utils;

import android.content.Context;

import com.bsit.pboard.calendarview.DateBean;

import java.util.ArrayList;
import java.util.List;

public class CalendarUtil {
    /**
     * 获得当月显示的日期（上月 + 当月 + 下月）
     *
     * @param year  当前年份
     * @param month 当前月份
     * @return
     */
    public static List<DateBean> getMonthDate(int year, int month) {
        List<DateBean> datas = new ArrayList<DateBean>();
        int week = SolarUtil.getFirstWeekOfMonth(year, month - 1);

        int lastYear;
        int lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        int lastMonthDays = SolarUtil.getMonthDays(lastYear, lastMonth);//上个月总天数

        int currentMonthDays = SolarUtil.getMonthDays(year, month);//当前月总天数

        int nextYear;
        int nextMonth;
        if (month == 12) {
            nextMonth = 1;
            nextYear = year + 1;
        } else {
            nextMonth = month + 1;
            nextYear = year;
        }

        if(week == 0){
            week = 6;
        }else{
            week = week - 1;
        }
        for (int i = 0; i < week; i++) {
            datas.add(initDateBean(lastYear, lastMonth, lastMonthDays - week + 1 + i, 0, 0));
        }

        for (int i = 0; i < currentMonthDays; i++) {
            datas.add(initDateBean(year, month, i + 1, 1, 0));
        }

        int nextWeek = 7 * getMonthRows(year, month) - currentMonthDays - week;
        if(nextWeek < 7){
            for (int i = 0; i < nextWeek; i++) {
                datas.add(initDateBean(nextYear, nextMonth, i + 1, 2, 0));
            }
        }
        return datas;
    }

    private static DateBean initDateBean(int year, int month, int day, int type, int showType) {
        DateBean dateBean = new DateBean();
        dateBean.setSolar(year, month, day);
        dateBean.setType(type);
        return dateBean;
    }

    /**
     * 计算当前月需要显示几行
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthRows(int year, int month) {
        int firstWeekDays = SolarUtil.getFirstWeekOfMonth(year, month - 1);
        if(firstWeekDays == 0){
            firstWeekDays = 6;
        }
        int items = firstWeekDays + SolarUtil.getMonthDays(year, month);
        int rows = items % 7 == 0 ? items / 7 : (items / 7) + 1;
        if (rows == 4) {
            rows = 5;
        }
        return rows;
    }

    public static int getPxSize(Context context, int size) {
        return size * context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getTextSize1(Context context, int size) {
        return (int) (size * context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 根据ViewPager position 得到对应年月
     *
     * @param position
     * @return
     */
    public static int[] positionToDate(int position, int startY, int startM) {
        int year = position / 12 + startY;
        int month = position % 12 + startM;

        if (month > 12) {
            month = month % 12;
            year = year + 1;
        }

        return new int[]{year, month};
    }
}