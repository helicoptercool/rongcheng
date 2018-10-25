package com.bsit.pboard.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class Chandler extends Handler {
    private CardOperatorListener mCopListener;
    public Chandler(CardOperatorListener listener){
        mCopListener = listener;
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int type = msg.what;
        Object obj = msg.obj;
//        switch (msg.what) {
//            case ConstantMsg.MSG_FIND_CARD:
//                removeMessages(ConstantMsg.MSG_FIND_CARD);
//                break;
//            default:
//                break;
//        }
        mCopListener.onCardOperate(type,obj);
    }
}
