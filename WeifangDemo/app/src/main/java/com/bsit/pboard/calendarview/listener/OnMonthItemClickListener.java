package com.bsit.pboard.calendarview.listener;

import android.view.View;
import com.bsit.pboard.calendarview.DateBean;

/**
 * 日期点击接口
 */
public interface OnMonthItemClickListener {
    /**
     * @param view
     * @param date
     */
    void onMonthItemClick(View view, DateBean date, int position);
}
