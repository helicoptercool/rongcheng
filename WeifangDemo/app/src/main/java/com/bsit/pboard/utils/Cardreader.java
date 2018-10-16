package com.bsit.pboard.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by shengbing on 2016/7/22.
 */
public class Cardreader {

    private UsbHid usbhid = null;
    private byte bseq = 0;

    public void openreader(Context context) {
        usbhid = UsbHid.getInstance(context);
        if(!isConnect()){
            if (usbhid.getUsbDevice()) {
                usbhid.ConnectUsbHid();
            }
        }
    }

    public boolean isConnect() {
        return usbhid.isConnect();
    }




    /**
     * 打开读卡器
     * bslot ==0  非接触卡
     * bslot ==1   PSAM1
     * bslot ===2  psam2
     * bslot ==0x0e 接触卡
     */
    public byte[] card_poweron(int bslot) {
        Log.e("SEND", "上电复位");
        byte[] src = new byte[512];
        src[0] = 0x62;
        src[1] = (byte) 0 & 0xff;
        src[2] = (byte) (0 >> 8) & 0xff;
        src[3] = (byte) (0 >> 16) & 0xff;
        src[4] = (byte) (0 >> 24) & 0xff;
        src[5] = (byte) bslot;
        src[6] = bseq++;
        src[7] = (byte) 0;
        src[8] = (byte) 0;
        src[9] = 0;

        int datalen = 0 + 10;
        byte[] datatosend = new byte[64];
        if (datalen > 0) {
            System.arraycopy(src, 0, datatosend, 0, datalen);
            usbhid.SendData(datatosend);
        }
        byte[] recvdata = null;
        do {
            recvdata = usbhid.RecData();
        }
        while (recvdata[0] != -128);
        if (recvdata == null) {
            return null;
        }
        int recvlen = recvdata[1] + recvdata[2] * 256 + recvdata[3] * 256 * 256 + recvdata[4] * 256 * 256 * 256;
        byte[] atr = null;
        recvlen += 10;
        if (recvlen < 64) {
            if (recvlen > 10) {
                atr = new byte[recvlen - 10];
                System.arraycopy(recvdata, 10, atr, 0, recvlen - 10);
            }
        }
        return atr;
    }

    /**
     * 发送指令给读卡器
     * @param bslot
     * @return
     */
    public byte[] sendapdu(int bslot, int cmdlen, byte[] cmd) {
        Log.e("SEND", ByteUtil.byte2HexStr(cmd));
        byte[] src = new byte[512];
        int offset = 0;
        src[0] = 0x6f;

        src[1] = (byte) (cmdlen & 0xff);
        src[2] = (byte) ((cmdlen >> 8) & 0xff);
        src[3] = (byte) ((cmdlen >> 16) & 0xff);
        src[4] = (byte) ((cmdlen >> 24) & 0xff);
        src[5] = (byte) bslot;
        src[6] = bseq++;
        src[7] = (byte) 0;
        src[8] = (byte) 0;
        src[9] = 0;
        System.arraycopy(cmd, 0, src, 10, cmdlen);
        int datalen = cmdlen + 10;
        byte[] datatosend = new byte[64];
        while (datalen > 0) {
            if (datalen > 64) {
                System.arraycopy(src, offset, datatosend, 0, 64);
                datalen -= 64;
                offset += 64;
            } else {
                System.arraycopy(src, offset, datatosend, 0, datalen);
                offset += datalen;
                datalen = 0;
            }

            usbhid.SendData(datatosend);
        }

        byte[] resp = new byte[512];
        int resplen = 0;
        byte[] recvdata = null;
        do {
            recvdata = usbhid.RecData();
        }
        while (recvdata[0] != -128);
        if (recvdata == null) {
            return null;
        }
        int recvlen = recvdata[1] + recvdata[2] * 256 + recvdata[3] * 256 * 256 + recvdata[4] * 256 * 256 * 256;
        if (recvlen == 0)
            return null;
        resplen = recvlen;
        int leftlen = recvlen;
        recvlen += 10;
        if (recvlen < 64) {
            System.arraycopy(recvdata, 10, resp, 0, recvlen - 10);
            leftlen = 0;
            resplen = recvlen - 10;
        } else {
            System.arraycopy(recvdata, 10, resp, 0, 64 - 10);
            leftlen -= (64 - 10);
            resplen = 64 - 10;
        }

        while (leftlen > 0) {
            recvdata = usbhid.RecData();
            if (leftlen > 64) {
                System.arraycopy(recvdata, 0, resp, resplen, 64);
                leftlen -= 64;
                resplen += 64;
            } else {
                System.arraycopy(recvdata, 0, resp, resplen, leftlen);
                leftlen -= leftlen;
                resplen += leftlen;
            }
        }
        byte[] ret_resp = new byte[resplen];
        System.arraycopy(resp, 0, ret_resp, 0, resplen);
        Log.e("RECEIVE", ByteUtil.byte2HexStr(ret_resp));
        return ret_resp;
    }

    /**
     * 关闭读卡器
     * @param bslot
     * @return
     */
    public int card_poweroff(int bslot) {
        byte[] src = new byte[512];
        src[0] = 0x63;
        src[1] = (byte) 0 & 0xff;
        src[2] = (byte) (0 >> 8) & 0xff;
        src[3] = (byte) (0 >> 16) & 0xff;
        src[4] = (byte) (0 >> 24) & 0xff;
        src[5] = (byte) bslot;
        src[6] = bseq++;
        src[7] = (byte) 0;
        src[8] = (byte) 0;
        src[9] = 0;

        int datalen = 0 + 10;
        byte[] datatosend = new byte[64];
        System.arraycopy(src, 0, datatosend, 0, datalen);
        while (datalen > 0) {
            if (datalen > 64)
                datalen -= 64;
            else
                datalen = 0;
            usbhid.SendData(datatosend);
        }
        byte[] recvdata = null;
        recvdata = usbhid.RecData();
        return 0;
    }

    public int closereader() {
        usbhid.closeDevice();
        return 0;
    }

}
