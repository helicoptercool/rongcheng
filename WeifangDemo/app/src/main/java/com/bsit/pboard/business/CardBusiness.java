package com.bsit.pboard.business;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bsit.pboard.R;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.utils.ByteUtil;

/**
 * Created by shengbing on 2016/7/22.
 */
public class CardBusiness {
    private static final String TAG = CardBusiness.class.getName();
    private static CardBusiness cardBusiness;
    private static CardReaderLib cardreader;
    private Context context;


    private CardBusiness(Context context) {
        cardreader = CardReaderLib.INSTANCE;
        cardreader.tty_init();
        this.context = context;
    }

    public static CardBusiness getInstance(Context context) {
        if (cardBusiness == null) {
            cardBusiness = new CardBusiness(context);
        }
        return cardBusiness;
    }

    /**
     * 寻卡
     */
    public String findCard() throws FindCardException {
        String response = sendApdu(ByteUtil.hexStr("62000000000100000000"), 10);
        Log.i("CardBusiness", "find card response ====== " + response);
        if (TextUtils.isEmpty(response) || response.length() < 16) {
            throw new FindCardException("寻卡失败");
        }
        return response.substring(response.length() - 16, response.length() - 8);
    }

    /**
     * 获取设备编号
     */
    public String getSN() throws FindCardException {
        String response = sendApdu(ByteUtil.hexStr("6C05000000FE00000000FF19001000"), 15);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new FindCardException("获取SN失败" + response);
        }
        return response.substring(0, response.length() - 4);
    }

    private byte[] makeCmd(String cmd) {
        byte length = (byte) (cmd.length() / 2);
        byte[] cmdLenArr = {length, 0, 0, 0};
        String lenStr = ByteUtil.byte2HexStr(cmdLenArr);
        String cmdStr = "6F" + lenStr + "0100000000" + cmd;
        return ByteUtil.hexStr(cmdStr);
    }


    public CardInfo getCardInfo() throws ReadCardException {
        String response = sendApdu(makeCmd("00A40000023f01"), 17);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException("选择主目录失败");
        }
        response = sendApdu(makeCmd("00A4040009A00000000386980701"), 24);
        Log.i("选择子目录", "选择子目录 = " + response);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000") || response.length() < 106) {
            throw new ReadCardException("选择子目录失败");
        }
        CardInfo mCardInfo = new CardInfo();
        mCardInfo.setCardSType(response.substring(104, 106));
        String sonType = response.substring(106, 108);
        mCardInfo.setCardMType(sonType);
        mCardInfo.setCardNo(response.substring(68, 84));
        mCardInfo.setStartTime(response.substring(84, 92));
        mCardInfo.setEndTime(response.substring(92, 100));
        mCardInfo.setIsUse(response.substring(118, 120).equals("01"));
        sendApdu(makeCmd("00B085000000"), 16);
        response = sendApdu(makeCmd("805C000204"), 15);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException("获取卡余额失败");
        }
        int balLen = response.length() - 4;
        String balance = response.substring(balLen - 8, balLen);
        mCardInfo.setBalance(balance);
        Log.i(TAG, "获取卡余额 = " + balance);
        return mCardInfo;
    }

    public CardInfo readOtherCard() throws ReadCardException {
        String response = sendApdu(makeCmd("00A404000E315041592E5359532E4444463031"), 29);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException(context.getString(R.string.str_get_main_dir_failure));
        }
        response = sendApdu(makeCmd("00A4040008A000000632010105"), 23);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000") || response.length() < 106) {
            throw new ReadCardException(context.getString(R.string.str_get_son_dir_failure));
        }
        CardInfo mCardInfo = new CardInfo();
        mCardInfo.setIsUse(true);
        mCardInfo.setCardIssuerLogo(response.substring(42, 58));
        mCardInfo.setAppTypeIdentification(response.substring(58, 60));
        mCardInfo.setAppVersionOrganization(response.substring(60, 62));
        mCardInfo.setCardNo(response.substring(63, 82));
        mCardInfo.setStartTime(response.substring(82, 90));
        mCardInfo.setEndTime(response.substring(90, 98));
        mCardInfo.setFciData(response.substring(98, 106));
//        response = sendApdu(ByteUtil.hexStr("6F05000000010000000000b085001e"), 15);
//        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
//            throw new ReadCardException("获取卡类型失败");
//        }
//        if(response.length() > 34){
//            mCardInfo.setCardSType(response.substring(32, 34));
//        }else{
//            mCardInfo.setCardSType("00");
//        }
        response = sendApdu(makeCmd("805C000204"), 15);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException("获取卡余额失败");
        }
        mCardInfo.setBalance(response.substring(0, 8));
        return mCardInfo;
    }


    public String getData0015() {
        String response = sendApdu(makeCmd("00A4040009A00000000386980702"), 24);
        if (response != null && response.endsWith("9000")) {
            int length = response.length();
            response = response.substring(0, length - 4);
            return response;
        }
        return "";
    }

    /**
     * 圈存初始化
     */
    public CardInfo getTopInitInfo(CardInfo mCardInfo, String reloadBal, String deviceId) throws Exception {
        sendApdu(makeCmd("00A4040009A00000000386980701"), 24);
        sendApdu(makeCmd("0020000003123456"), 18);
        String initTopMsg = "6F110000000100000000805000020B01" + ByteUtil.appendLengthForMessage(Long.toHexString(Long.parseLong(reloadBal)), 8) + deviceId + "10";
        String response = sendApdu(ByteUtil.hexStr(initTopMsg), 27);
        Log.i("圈存初始化", "圈存初始化 response = " + response);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new Exception("圈存初始化失败:" + response);
        }
        mCardInfo.setBalance(response.substring(0,8));
        mCardInfo.setCardTradeNo(response.substring(8, 12));
        mCardInfo.setKeyVer(response.substring(12, 14));
        mCardInfo.setAlglnd(response.substring(14, 16));
        mCardInfo.setCardRand(response.substring(16, 24));
        mCardInfo.setQcMac(response.substring(24, 32));
        return mCardInfo;
    }

    public CardInfo testGetTopInitInfo(CardInfo mCardInfo, String reloadBal, String deviceId) throws Exception {
//        sendApdu(makeCmd("0020000003123456"), 18);
        //00A4040008b7e2b1d5cab3ccc3
        //0020000003123456
        String str = sendApdu(makeCmd("00A4040008b7e2b1d5cab3ccc3"), 23);
        Log.i(TAG, "00A4040008b7e2b1d5cab3ccc3 return = " + str);
        str = sendApdu(makeCmd("0020000003123456"), 18);
        Log.i(TAG, "123456 return = " + str);
        Log.i(TAG, "reloadBal = " + reloadBal + ", deviceId = " + deviceId);
        String initTopMsg = "6F110000000100000000805000020B01" + ByteUtil.appendLengthForMessage(Long.toHexString(Long.parseLong(reloadBal)), 8) + deviceId + "10";
        String response = sendApdu(ByteUtil.hexStr(initTopMsg), 27);
        Log.i("圈存初始化", "圈存初始化 response = " + response);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new Exception("圈存初始化失败:" + response);
        }

        mCardInfo.setBalance(response.substring(0,8));
        mCardInfo.setCardTradeNo(response.substring(8, 12));
        mCardInfo.setKeyVer(response.substring(12, 14));
        mCardInfo.setAlglnd(response.substring(14, 16));
        mCardInfo.setCardRand(response.substring(16, 24));
        mCardInfo.setQcMac(response.substring(24, 32));
        return mCardInfo;
    }


    public CardInfo testGetTacFormTopUp(String cmdTop, CardInfo cardInfo, String reloadAmount) throws Exception {
        Log.i("qqqqqqqqqqqqqqqqqqqqqq", "qqqqqqqqqqq");
        String response = sendApdu(ByteUtil.hexStr("6F110000000100000000805200000B" + cmdTop + "04"), 27);
        Log.i("QQQQQQQQQq", "QQQQQQQQQQQQQ");
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new Exception("圈存失败:" + response);
        }
        reloadAmount = "100";
        cardInfo.setTac(response.substring(0, 8));
        response = sendApdu(makeCmd("805C000204"), 5);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            response = (ByteUtil.appendLengthForMessage(Long.toHexString(Long.parseLong(reloadAmount) + ByteUtil.pasInt(cardInfo.getBalance())), 8)) + response;
        }
        cardInfo.setBalance(response.substring(0, 8));
        return cardInfo;
    }

    /**
     * 圈存
     */
    public CardInfo getTacFormTopUp(String cmdTop, CardInfo cardInfo, String reloadAmount) throws Exception {
        Log.i("qqqqqqqqqqqqqqqqqqqqqq", "qqqqqqqqqqq");
        String response = sendApdu(ByteUtil.hexStr("6F110000000100000000805200000B" + cmdTop + "04"), 27);
        Log.i("QQQQQQQQQq", "QQQQQQQQQQQQQ");
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new Exception("圈存失败:" + response);
        }
        cardInfo.setTac(response.substring(0, 8));
        response = sendApdu(makeCmd("805C000204"), 5);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            response = (ByteUtil.appendLengthForMessage(Long.toHexString(Long.parseLong(reloadAmount) + ByteUtil.pasInt(cardInfo.getBalance())), 8)) + response;
        }
        cardInfo.setBalance(response.substring(0, 8));
        return cardInfo;
    }

    /**
     * 关闭设备
     */
    public static void closeDevice() {
        cardreader.tty_exit();
    }


    public static class FindCardException extends Exception {


        public FindCardException() {
        }

        public FindCardException(String message) {
            super(message);
        }
    }

    public static class ReadCardException extends Exception {
        public ReadCardException() {
        }

        public ReadCardException(String message) {
            super(message);
        }
    }


    private String sendApdu(byte[] pack, int len) {
        String resp;
        int code = cardreader.send_pack(pack, len);
        Log.e("SENDAPDU", "SENDAPDU:" + ByteUtil.byte2HexStr(pack));
        if (code == len) {
            byte[] respDate = new byte[128];
            code = cardreader.read_pack(respDate);
            Log.e("READ_PACK", "READ_PACK:" + ByteUtil.byte2HexStr(respDate));
            if (code > 0) {
                resp = ByteUtil.byte2HexStr(respDate).substring(20, code * 2);
            } else {
                resp = "" + code;
            }
        } else {
            resp = code + "";
        }
        return resp;
    }


    public CardInfo testReadOtherCard() throws ReadCardException {
        String response = sendApdu(makeCmd("00A404000E315041592E5359532E4444463031"), 29);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException(context.getString(R.string.str_get_main_dir_failure));
        }
        //00A4000002BF11
        response = sendApdu(makeCmd("00A4000002BF11"), 17);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException(context.getString(R.string.str_get_son_dir_failure));
        }
        CardInfo mCardInfo = new CardInfo();
        mCardInfo.setIsUse(true);
        Log.i(TAG, "00A4000002BF11  return ok");
//        mCardInfo.setCardIssuerLogo(response.substring(42, 58));
//        mCardInfo.setAppTypeIdentification(response.substring(58, 60));
//        mCardInfo.setAppVersionOrganization(response.substring(60, 62));
//        mCardInfo.setCardNo(response.substring(63, 82));
//        mCardInfo.setStartTime(response.substring(82, 90));
//        mCardInfo.setEndTime(response.substring(90, 98));
//        mCardInfo.setFciData(response.substring(98, 106));

//        response = sendApdu(ByteUtil.hexStr("6F05000000010000000000b085001e"), 15);
//        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
//            throw new ReadCardException("获取卡类型失败");
//        }
//        if(response.length() > 34){
//            mCardInfo.setCardSType(response.substring(32, 34));
//        }else{
//            mCardInfo.setCardSType("00");
//        }
//        response = sendApdu(makeCmd("805C000204"), 15);
//        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
//            throw new ReadCardException("获取卡余额失败");
//        }
//        mCardInfo.setBalance(response.substring(0, 8));
        return mCardInfo;
    }


}
