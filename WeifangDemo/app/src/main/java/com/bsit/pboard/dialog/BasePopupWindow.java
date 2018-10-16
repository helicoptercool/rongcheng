package com.bsit.pboard.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.bsit.pboard.R;


public class BasePopupWindow extends PopupWindow {
    protected Button winCancleBtn;// 取消
    protected Button winSubmitBtn;// 确定，需要获取设置的值

    protected View view;
    protected onSubmitListener onSubmit;

    protected onItemSubmitListener onItemSubmit;

    public BasePopupWindow(Context context) {
        super(context);

    }

    public interface onSubmitListener {
        void onSubmit(String text);
    }

    public interface onItemSubmitListener {
        void onItemSubmit(String text, int position);
    }

    public interface onSubmitClickListener {
        void onSubmit(String hour, String minute, char[] repeat);
    }

    public void setViw() {

        this.setContentView(view);// 设置SelectPicPopupWindow的View

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置SelectPicPopupWindow弹出窗体的宽

        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);// 设置SelectPicPopupWindow弹出窗体的高

        this.setFocusable(true);// 设置SelectPicPopupWindow弹出窗体可点击

        this.setAnimationStyle(R.style.AnimBottom);// 设置SelectPicPopupWindow弹出窗体动画效果

        ColorDrawable dw = new ColorDrawable(0x53000000);// 实例化一个ColorDrawable颜色为半透明

        this.setBackgroundDrawable(dw);// 设置SelectPicPopupWindow弹出窗体的背景
    }

}
