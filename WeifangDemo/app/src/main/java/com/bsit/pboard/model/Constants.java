package com.bsit.pboard.model;

public class Constants {

    private static final String URL_RONGCHENG_BASE = "http://121.43.37.101:16012"; //榕城alicloud
    //    private static final String URL_RONGCHENG_BASE = "http://192.168.1.209:8080"; //榕城测试地址
    public static final String URL_SIGN_IN = URL_RONGCHENG_BASE + "/rongchengtong/recharge/sign"; //签到获取秘钥
    public static final String URL_QUERY_ORDER = URL_RONGCHENG_BASE + "/rongchengtong/recharge/queryUnRechargeOrders"; //待补登订单查询
    public static final String URL_RECHARGE_APPLY = URL_RONGCHENG_BASE + "/rongchengtong/recharge/rechargeApply"; //圈存申请
    public static final String URL_MONTY_TICKET_UPDATE = URL_RONGCHENG_BASE + "/rongchengtong/recharge/updateMonthTicket"; //CPU卡补登月票有效期修改（月票才需）
    public static final String URL_RECHARGE_CONFIRM = URL_RONGCHENG_BASE + "/rongchengtong/recharge/rechargeConfirm"; //圈存确认

    //    public static final String DEVICE_ID = "040110010001";
    public static final String DEVICE_ID = "010010030006"; //test


    public static final String TEST_GET_MAC2 = "http://192.168.1.177:9001/getMac2";
}
