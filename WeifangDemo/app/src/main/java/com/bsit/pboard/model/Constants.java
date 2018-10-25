package com.bsit.pboard.model;

public class Constants {

    //充值接口前缀
    private static String PRE_URL = "http://121.43.37.101:16012/jx_bus_android/";

    //2012 补登查询
    public static String QUERY_RECHARGEINFO_URL = PRE_URL + "recharge/queryRechargeInfo";

    //2061 补登圈存
    public static String START_RECHARGECARD_URL = PRE_URL + "recharge/startRechargeCard";

    //2062 补登确认
    public static String CONFIRM_RECHARGEINFO_URL = PRE_URL + "recharge/confirmRechargeInfo";

    //心跳接口
    public static String HEART_BEAT_URL = "http://192.168.1.120:9080/equ_watch/httpHeartBeat/saveHeartBeat";


    private static final String URL_RONGCHENG_BASE = "http://192.168.1.207:8080"; //榕城测试地址
    public static final String URL_SIGN_IN = URL_RONGCHENG_BASE + "/rongchengtong/recharge/sign"; //签到获取秘钥
    public static final String URL_QUERY_ORDER = URL_RONGCHENG_BASE + "/rongchengtong/recharge/queryUnRechargeOrders"; //待补登订单查询
    public static final String URL_RECHARGE_APPLY = URL_RONGCHENG_BASE + "/rongchengtong/recharge/rechargeApply"; //圈存申请
    public static final String URL_MONTY_TICKET_UPDATE = URL_RONGCHENG_BASE + "/rongchengtong/recharge/updateMonthTicket"; //CPU卡补登月票有效期修改（月票才需）
    public static final String URL_RECHARGE_CONFIRM = URL_RONGCHENG_BASE + "/rongchengtong/recharge/rechargeConfirm"; //圈存确认

//    public static final String DEVICE_ID = "040110010001";
    public static final String DEVICE_ID = "010010030006"; //test
}
