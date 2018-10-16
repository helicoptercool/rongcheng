package com.bsit.pboard.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsit.pboard.R;
import com.bsit.pboard.calendarview.listener.OnMonthItemClickListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MonthView extends ViewGroup {

    private static final int ROW = 6;
    private static final int COLUMN = 7;

    private Context mContext;
    private boolean showLastNext = true;
    private boolean disableBefore = false;
    private int colorSolar = Color.BLACK;
    private int colorLunar = Color.parseColor("#999999");
    //    private int sizeSolar = 14;//sp
    private float sizePx;//px
    private OnMonthItemClickListener itemClickListener;
    private float curX, initX;
    private FillLister fillLister;

    public void setFillLister(FillLister fillLister) {
        this.fillLister = fillLister;
    }

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        mContext = context;
        setBackgroundColor(Color.WHITE);
    }

    /**
     * @param dates 需要展示的日期数据
     */
    public void setDateList(List<DateBean> dates) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        SimpleDateFormat df = new SimpleDateFormat("y-M-d");
        String thisDay = df.format(new Date());
        for (int i = 0; i < dates.size(); i++) {
            final DateBean date = dates.get(i);

            if (date.getType() == 0) {
                if (!showLastNext) {
                    addView(new View(mContext), i);
                    continue;
                }
            }

            if (date.getType() == 2) {
                if (!showLastNext) {
                    addView(new View(mContext), i);
                    continue;
                }
            }

            View view;
            TextView solarDay;//阳历TextView
            TextView dayState;//阳历TextView
            view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, null);
            solarDay = (TextView)view.findViewById(R.id.solar_day);
            dayState = (TextView)view.findViewById(R.id.tv_day_state);
            if (i % 7 == 5 || i % 7 == 6) {
                solarDay.setTextColor(getResources().getColor(R.color.color_FE4F20));
            } else {
                solarDay.setTextColor(colorSolar);
            }
            sizePx = solarDay.getTextSize() + dayState.getTextSize();
            boolean isToday = thisDay.equals(date.getSolar()[0] + "-" + date.getSolar()[1] + "-" + date.getSolar()[2]);
            //设置上个月和下个月的阳历颜色
            if (date.getType() == 0 || date.getType() == 2) {
                if (i % 7 == 5 || i % 7 == 6) {
                    solarDay.setTextColor(getResources().getColor(R.color.color_FFBCAA));
                } else {
                    solarDay.setTextColor(colorLunar);
                }
            }
            solarDay.setText(String.valueOf(date.getSolar()[2]));

            if (date.getType() == 1) {
                view.setTag(date.getSolar()[2]);
                if (disableBefore) {
                    solarDay.setTextColor(colorLunar);
                    view.setTag(-1);
                    addView(view, i);
                    continue;
                }
            }

            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (date.getType() != 1) {
                        return;
                    }
                    if (itemClickListener != null) {
                        itemClickListener.onMonthItemClick(v, date, finalI);
                    }
                }
            });
            addView(view, i);
        }
        requestLayout();
    }

    /**
     * 设置日期点击回调
     *
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnMonthItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int itemWidth = widthSpecSize / COLUMN;
        int width = widthSpecSize;
        int height = (int) (sizePx * 1.7) * ROW;
        setMeasuredDimension(width, height);
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec((int) (sizePx * 2.0), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }
        View childView = getChildAt(0);
        int itemWidth = childView.getMeasuredWidth();
        int itemHeight = childView.getMeasuredHeight();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int left = i % COLUMN * itemWidth;
            int top = i / COLUMN * (itemHeight);
            int right = left + itemWidth;
            int bottom = top + itemHeight;
            view.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            initX = motionEvent.getX();
            Log.e("initX", "initX is : " + initX);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            curX = motionEvent.getX();
            Log.e("curX", "curX is : " + curX);
            float jx = curX - initX;
            if (Math.abs(jx) > 25) {
                if (jx > 0) {
                    //向左滑动 上一个
                    if (fillLister != null) {
                        fillLister.fillLeft();
                    }
                } else {
                    //向右滑动 下一个
                    if (fillLister != null) {
                        fillLister.fillRight();
                    }
                }
                return true;
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public interface FillLister {
        void fillLeft();

        void fillRight();
    }
}