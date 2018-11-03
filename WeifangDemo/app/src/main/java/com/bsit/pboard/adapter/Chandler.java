package com.bsit.pboard.adapter;

import android.os.Handler;
import android.os.Message;

import com.bsit.pboard.business.HttpBusiness;
import com.bsit.pboard.constant.ConstantMsg;

public class Chandler extends Handler {
    private CardOperatorListener mCopListener;
    private YearCheckListener mYearCheckListener;

    public Chandler(CardOperatorListener listener) {
        mCopListener = listener;
    }

    public void setYearCheckListener(YearCheckListener listener){
        mYearCheckListener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int type = msg.what;
        Object obj = msg.obj;
        switch (msg.what) {
            case HttpBusiness.YEAR_CHECK_QUERY_SUCCESS:
                mYearCheckListener.onYearCheck(ConstantMsg.TYPE_YEAR_CHECK_QUERY,ConstantMsg.TYPE_SUCCESS,obj);
                break;
            case HttpBusiness.YEAR_CHECK_QUERY_FAILURE:
                mYearCheckListener.onYearCheck(ConstantMsg.TYPE_YEAR_CHECK_QUERY,ConstantMsg.TYPE_FALURE,obj);
                break;
            case HttpBusiness.YEAR_CHECK_APPLY_SUCCESS:
                mYearCheckListener.onYearCheck(ConstantMsg.TYPE_YEAR_CHECK_APPLY,ConstantMsg.TYPE_SUCCESS,obj);
                break;
            case HttpBusiness.YEAR_CHECK_APPLY_FAILURE:
                mYearCheckListener.onYearCheck(ConstantMsg.TYPE_YEAR_CHECK_APPLY,ConstantMsg.TYPE_FALURE,obj);
                break;
            case HttpBusiness.YEAR_CHECK_NOTICE_SUCCESS:
                mYearCheckListener.onYearCheck(ConstantMsg.TYPE_YEAR_CHECK_NOTICE,ConstantMsg.TYPE_SUCCESS,obj);
                break;
            case HttpBusiness.YEAR_CHECK_NOTICE_FAILURE:
                mYearCheckListener.onYearCheck(ConstantMsg.TYPE_YEAR_CHECK_NOTICE,ConstantMsg.TYPE_FALURE,obj);
                break;
            case ConstantMsg.MSG_FIND_CARD:
                removeMessages(ConstantMsg.MSG_FIND_CARD);
            default:
                mCopListener.onCardOperate(type, obj);
                break;
        }
    }
}
