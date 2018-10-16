package com.bsit.pboard.calendarview;

public class DateBean {
    private int[] solar;//阳历年、月、日
    private int type;//0:上月，1:当月，2:下月

    public int[] getSolar() {
        return solar;
    }

    public void setSolar(int year, int month, int day) {
        this.solar = new int[]{year, month, day};
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
