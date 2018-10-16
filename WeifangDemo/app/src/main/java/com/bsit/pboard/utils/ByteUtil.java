package com.bsit.pboard.utils;

import android.text.TextUtils;

/**
 * Created by shengbing on 2016/7/22.
 */
public class ByteUtil {

	/**
	 * bytes转换成十六进制字符串
	 */
	public static String byte2HexStr(byte[] a) {
		if(a == null){
			return null;
		}
		String hs = "";
		String stmp = "";
		for (int n = 0; n < a.length; n++) {
			stmp = (Integer.toHexString(a[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	/**
	 * 16进制字符串转换成int数组
	 *
	 * @param str
	 * @return
	 */
	public static byte[] hexStr(String str) {

		byte[] bt = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			bt[i] = (byte) Integer.parseInt(str.substring(i * 2, 2 * (i + 1)),
					16);
		}
		return bt;
	}


	public static String toAmountString(float value) {
		return String.format("%.2f", value);
	}

	/**
	 *
	 * @param len
	 * @return
	 */
	public static String appendLengthForMessage(String msg,int len) {
		if(TextUtils.isEmpty(msg)){
			return String.format("%0" + len + "d", 0);
		}
		if(len <= msg.length()){
			return msg;
		}
		int length = len - msg.length();
		return String.format("%0" + length + "d%s", 0, msg);
	}

	/**
	 *公交卡号后八位转换
	 * @param asc
	 * @return
	 */
	public static String AscToHex(String asc)
	{
		if (TextUtils.isEmpty(asc)) return null;
		int[] r = new int [4];

		for (int i = 0; i < 4; i += 1)
		{
			String ss = asc.substring(i*2, 2+(i*2));
			r[i] = (int) pasInt(ss);
		}
		int j = r[0] * 256*256*256 + r[1] * 256*256 + r[2] * 256 + r[3];
		return String.valueOf(j);
	}


	/**
	 *
	 * @Title: pasInt
	 * @Description: TODO(16进制在字符转10进制)
	 * @param @param msg
	 * @param @return    设定文件
	 * @return int    返回类型
	 * @throws
	 */
	public static long pasInt(String msg) {
		long a = 0;
		for (int i = 0; i < msg.length(); i++) {

			a =  a + Integer.parseInt(msg.substring(i, i + 1), 16)
					* ((long)Math.pow(16, (msg.length() - (i + 1))));

		}
		return a;
	}
}
