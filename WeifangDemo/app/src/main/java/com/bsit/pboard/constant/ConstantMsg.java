package com.bsit.pboard.constant;

public class ConstantMsg {
    public static final int VIEW_STICK_CARD = 0;
    public static final int VIEW_INVALID_CARD = 1;
    public static final int VIEW_NO_ORDER = 2;
    public static final int VIEW_CHARAGING = 3;
    public static final int VIEW_CHARGE_END = 4;
    public static final int VIEW_NET_ERROR = 5;
    public static final int VIEW_SHOW_CARD = 6;
    public static final int VIEW_CARD_NOT_USE = 7;
    public static final int VIEW_INIT_NETWORK = 8;
    public static final int VIEW_UNRECOGNIZE_CARD = 9;
    public static final int VIEW_NETWORK_OK = 81;

    public final static int MSG_UPDATE_TIME = 10;
    public final static int MSG_FIND_CARD = 11;
    public final static int MSG_FAIL_READ_CARD = 12;
    public final static int MSG_FAIL_RECHARGE = 13;
    public final static int MSG_FAIL_RECHARGE_CONFIRM = 14;
    public final static int MSG_HAS_RECHERGED = 15;
    public final static int MSG_CARD_CHANGE_TYPE = 16;
    public final static int MSG_HEART_BEAT = 17;

    public static final int TYPE_YEAR_CHECK_QUERY = 20;
    public static final int TYPE_YEAR_CHECK_APPLY = 21;
    public static final int TYPE_YEAR_CHECK_NOTICE = 22;

    public static final int TIME_INTEVAL_ONE_SECOND = 1000;
    public static final int TIME_INTEVAL_THREE_SECOND = 3000;
    public static final int TIME_INTEVAL_SIX_SECOND = 6000;

    public static final int TIME_INTEVAL_INIT_TIME = 1000;
    public static final int TIME_INTEVAL_FIND_CARD = 1000;
    public static final int TIME_INTEVAL_NET_CHECK = 6000;
    public static final int TIME_INTEVAL_HEART_BEAT = 600000;

    public static final int CARD_TYPE_TRANSPORT = 200; //交通部
    public static final int CARD_TYPE_HOUSE = 100; //住建部

    public static final String TRADE_TYPE_WALLET = "01";
    public static final String TRADE_TYPE_MONTH_TICKET = "02";

    public static final String CARD_TYPE_OLD = "36"; //0x36 老年卡

    public static final String TYPE_SUCCESS = "00000";
    public static final String TYPE_FALURE = "11111";
}
