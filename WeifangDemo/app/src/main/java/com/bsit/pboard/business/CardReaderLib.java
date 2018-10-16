package com.bsit.pboard.business;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CardReaderLib extends Library {
    CardReaderLib INSTANCE = (CardReaderLib) Native.loadLibrary("serial", CardReaderLib.class);

    /**
     * 函数名 : tty_init
     * 描述   : 初始化串口
     * 返回   : 0 成功； 其他 失败
     */
    int  tty_init();


    /**
     * 函数名 : tty_exit
     * 描述   : 串口退出
     * 返回   : 0 成功； 其他 失败
     */
    int  tty_exit();


    /**
     * 函数名 : send_pack
     * 描述   : RDA下发命令
     * 返回   : 发送数据包成功的字节数
     * 说明   : 如果返回的值与真实传递数据包长度不同，说明下发命令有误
     */
    int  send_pack(byte[] pack, int len);


    /**
     * 函数名 : read_pack
     * 描述   : 底板应答数据命令
     * 输入   :
     * 输出   :
     * 返回   : 接收数据包成功的字节数
     * 说明   :
     */
    int  read_pack(byte[]  pack);

}
