package com.bsit.pboard.business;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bsit.pboard.CrashApplication;
import com.bsit.pboard.R;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.CircleDepositInitRes;
import com.bsit.pboard.utils.ByteUtil;
import com.bsit.pboard.utils.Cardreader;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shengbing on 2016/7/22.
 */
public class CardBusiness {

    private static CardBusiness cardBusiness;
    private static CardReaderLib cardreader;
    private static Context context;


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
    public static String findCard() throws FindCardException {
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
    public static String getSN() throws FindCardException {
        String response = sendApdu(ByteUtil.hexStr("6C05000000FE00000000FF19001000"), 15);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new FindCardException("获取SN失败" + response);
        }
        return response.substring(0, response.length() - 4);
    }

    private static byte[] makeCmd(String cmd) {
        byte length = (byte) (cmd.length() / 2);
        byte[] cmdLenArr = {length, 0, 0, 0};
        String lenStr = ByteUtil.byte2HexStr(cmdLenArr);
        String cmdStr = "6F" + lenStr + "0100000000" + cmd;
        byte[] pdu = ByteUtil.hexStr(cmdStr);
        return pdu;
    }



    public static CardInfo getCardInfo() throws ReadCardException {

//        String first = sendApdu(ByteUtil.hexStr("6F0500000001000000000012000000"), 15);
        //00 a4 00 00 02 3f 01
//        String response = sendApdu(ByteUtil.hexStr("6F07000000010000000000A40000023f01"), 17);
        String response = sendApdu(makeCmd("00A40000023f01"), 17);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException("选择主目录失败");
        }

        //00 a4 04 00 09 a0 00 00 00 03 86 98 07 01
//        response = sendApdu(ByteUtil.hexStr("6F0E000000010000000000A4040009A00000000386980701"), 24);
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
//        sendApdu(ByteUtil.hexStr("6F06000000010000000000B085000000"), 16);
        sendApdu(makeCmd("00B085000000"), 16);
//        response = sendApdu(ByteUtil.hexStr("6F050000000100000000805C000204"), 15);
        response = sendApdu(makeCmd("805C000204"), 15);

        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException("获取卡余额失败");
        }
        int balLen = response.length() - 4;
        String balance = response.substring(balLen - 8, balLen);
        mCardInfo.setBalance(balance);

        Log.i("获取卡余额", "return = " + response);
        Log.i("获取卡余额", "获取卡余额 = " + balance);
        return mCardInfo;
    }

    public static CardInfo readOtherCard() throws ReadCardException {
        Log.i("readOtherCardreadOther", "-------------readOtherCardreadOtherCardreadOtherCard");
//        String response = sendApdu(ByteUtil.hexStr("6F13000000010000000000A404000E315041592E5359532E4444463031"), 29);
        String response = sendApdu(makeCmd("00A404000E315041592E5359532E4444463031"), 29);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException(context.getString(R.string.str_get_main_dir_failure));
        }
        //A000000632010105
//        response = sendApdu(ByteUtil.hexStr("6F0D000000010000000000A4040008A000000632010105"), 23);
        response = sendApdu(makeCmd("00A4040008A000000632010105"), 23);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000") || response.length() < 106) {
            throw new ReadCardException(context.getString(R.string.str_get_son_dir_failure));
        }
        CardInfo mCardInfo = new CardInfo();
//        mCardInfo.setCardMType("01");
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


//        response = sendApdu(ByteUtil.hexStr("6F050000000100000000805C000204"), 15);
        response = sendApdu(makeCmd("805C000204"), 15);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new ReadCardException("获取卡余额失败");
        }
        mCardInfo.setBalance(response.substring(0, 8));
        return mCardInfo;
    }


    public static String getData0015() {
//        String response = sendApdu(ByteUtil.hexStr("6F0E000000010000000000A4040009A00000000386980702"), 24);
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
     *
     * @return
     * @throws Exception
     */
    public static CardInfo getTopInitInfo(CardInfo mCardInfo, String reloadBal, String deviceId) throws Exception {

//        5.0020000003123456
//        6.805000020B01000022B800002017121410

        //pin check
//        String checkRsp = sendApdu(ByteUtil.hexStr("6F0800000001000000000020000003123456"), 18);
        String checkRsp = sendApdu(makeCmd("0020000003123456"), 18);
        Log.i("pin pin", "ppppppiiiiiiiiinnnnnn response = " + checkRsp);
//        String initTopMsg = "6F110000000100000000805000020B01" + ByteUtil.appendLengthForMessage(Long.toHexString(Long.parseLong(reloadBal)), 8) + deviceId + "10";
        String initTopMsg = "6F110000000100000000805000020B01" + ByteUtil.appendLengthForMessage(Long.toHexString(Long.parseLong(reloadBal)), 8) + deviceId + "10";
        String response = sendApdu(ByteUtil.hexStr(initTopMsg), 27);
        Log.i("圈存初始化", "圈存初始化 response = " + response);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new Exception("圈存初始化失败:" + response);
        }
        /*CircleDepositInitRes cdInit = new CircleDepositInitRes();
        String oldBalance = response.substring(0,8);
        String transactSn = response.substring(8,12);
        String secretKeyV = response.substring(12,14);
        String alogrithId = response.substring(14,16);
        String prandomNum = response.substring(16,24);
        String macOne = response.substring(24,32);

        cdInit.setmOldBalance(oldBalance);
        cdInit.setmTransactSn(transactSn);
        cdInit.setmSecretKeyVer(secretKeyV);
        cdInit.setmAlgorithId(alogrithId);
        cdInit.setmPseudorandomNumber(prandomNum);
        cdInit.setmMacOne(macOne);*/

        mCardInfo.setCardSeq(response.substring(8, 12));
        mCardInfo.setKeyVer(response.substring(12, 14));
        mCardInfo.setAlglnd(response.substring(14, 16));
        mCardInfo.setCardRand(response.substring(16, 24));
        mCardInfo.setQcMac(response.substring(24, 32));


        return mCardInfo;
    }


    /**
     * 圈存
     *
     * @return
     * @throws Exception
     */
    public static CardInfo getTacFormTopUp(String cmdTop, CardInfo cardInfo, String reloadAmount) throws Exception {
        String response = sendApdu(ByteUtil.hexStr("6F110000000100000000805200000B" + cmdTop + "04"), 27);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            throw new Exception("圈存失败:" + response);
        }
        cardInfo.setTac(response.substring(0, 8));
//        response = sendApdu(ByteUtil.hexStr("6F050000000100000000805C000204"), 5);
        response = sendApdu(makeCmd("805C000204"), 5);
        if (TextUtils.isEmpty(response) || !response.endsWith("9000")) {
            response = (ByteUtil.appendLengthForMessage(Long.toHexString(Long.parseLong(reloadAmount) + ByteUtil.pasInt(cardInfo.getBalance())), 8)) + response;
        }
        cardInfo.setBalance(response.substring(0, 8));
        return cardInfo;
    }

    /**
     * 关闭设备
     *
     * @return
     * @throws Exception
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


    private static String sendApdu(byte[] pack, int len) {
        String resp = "";
        int code = cardreader.send_pack(pack, len);
        Log.e("SENDAPDU", "SENDAPDU:" + ByteUtil.byte2HexStr(pack));
        Log.e("SENDAPDU ", "SENDAPDU CODE:" + code);
        if (code == len) {
            byte[] respDate = new byte[128];
            code = cardreader.read_pack(respDate);
            Log.e("READ_PACK", "READ_PACK:" + ByteUtil.byte2HexStr(respDate));
            Log.e("READ_PACK ", "READ_PACK CODE:" + code);
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
}
