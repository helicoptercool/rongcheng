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

}
