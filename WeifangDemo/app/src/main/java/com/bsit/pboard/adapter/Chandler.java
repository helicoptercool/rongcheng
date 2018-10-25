package com.bsit.pboard.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bsit.pboard.MainActivity;
import com.bsit.pboard.R;
import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.model.CardInfo;
import com.bsit.pboard.model.MessageApplyWriteRes;
import com.bsit.pboard.model.MessageQueryRes;
import com.bsit.pboard.model.MonthTicketModifyRes;
import com.bsit.pboard.model.Rda;
import com.bsit.pboard.utils.ConstantMsg;
import com.bsit.pboard.utils.ToastUtils;
import com.bsit.pboard.utils.UpdateUtils;

public class Chandler extends Handler {
    private Context mContext;
    private CardOperatorListener mCopListener;
    public Chandler(Context context, CardOperatorListener listener){
        mContext = context;
        mCopListener = listener;
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int type = msg.what;
        Object obj = msg.obj;
        switch (msg.what) {
            case ConstantMsg.FIND_CARD_MSG_WHAT:
                removeMessages(ConstantMsg.FIND_CARD_MSG_WHAT);
                break;
            default:
                break;
        }
        mCopListener.onCardOperate(type,obj);
    }
}
