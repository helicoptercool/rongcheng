package com.bsit.pboard.constant;

public class Constants {

    private static final String URL_RONGCHENG_BASE = "http://121.43.37.101:16012"; //榕城alicloud
    //    private static final String URL_RONGCHENG_BASE = "http://192.168.1.209:8080"; //榕城测试地址
    public static final String URL_SIGN_IN = URL_RONGCHENG_BASE + "/rongchengtong/recharge/sign"; //签到获取秘钥
    public static final String URL_QUERY_ORDER = URL_RONGCHENG_BASE + "/rongchengtong/recharge/queryUnRechargeOrders"; //待补登订单查询
    public static final String URL_RECHARGE_APPLY = URL_RONGCHENG_BASE + "/rongchengtong/recharge/rechargeApply"; //圈存申请
    public static final String URL_MONTY_TICKET_UPDATE = URL_RONGCHENG_BASE + "/rongchengtong/recharge/updateMonthTicket"; //CPU卡补登月票有效期修改（月票才需）
    public static final String URL_RECHARGE_CONFIRM = URL_RONGCHENG_BASE + "/rongchengtong/recharge/rechargeConfirm"; //圈存确认
    public static final String URL_QUERY_YEAR_CHECK = URL_RONGCHENG_BASE + "/rongchengtong/queryYearCheckInfo"; //年检查询
    public static final String URL_APPLY_YEAR_CHECK = URL_RONGCHENG_BASE + "/rongchengtong/applyYearCheckInfo"; //年检请求
    public static final String URL_NOTICE_YEAR_CHECK = URL_RONGCHENG_BASE + "/rongchengtong/noticeYearCheckInfo"; //年检通知

    public static final String URL_UPLOAD_BASE = "http://121.43.37.101:9003/pre_service";
    public static final String URL_HEART_BEAT = URL_UPLOAD_BASE + "/api_device/saveHeartBeat"; // /api_device/saveHeartBeat  /api/saveHeartBeat
    public static final String URL_DOWN_LOAD_FILE = URL_UPLOAD_BASE + "/api/downFile";

    //    public static final String DEVICE_ID = "040110010001";
    public static final String DEVICE_ID = "010010030006"; //test
}
