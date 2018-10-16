package com.bsit.pboard.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsit.pboard.R;


public class LoadDialog extends AlertDialog {

    private TextView tips_loading_msg;
    private TextView loadInfoTv;
    private ImageView image;

    private String message = null;
    private String info = null;
    Context context;


    public LoadDialog(Context context, String message, String info) {
        super(context, R.style.loadingDialog);
        this.message = message;
        this.info = info;
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_load);
        tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
        loadInfoTv = (TextView) findViewById(R.id.tips_loading_info);
        image = (ImageView) findViewById(R.id.image);

        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.anim_loading);
        // 使用ImageView显示动画
        image.startAnimation(hyperspaceJumpAnimation);
        tips_loading_msg.setText(this.message);
        loadInfoTv.setText(this.info);
    }

    public void setText(String message) {
        this.message = message;
        tips_loading_msg.setText(this.message);
    }

    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }
}
