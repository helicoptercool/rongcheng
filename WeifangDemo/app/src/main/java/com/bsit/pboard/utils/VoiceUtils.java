package com.bsit.pboard.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.bsit.pboard.R;

import java.io.IOException;

public class VoiceUtils {
    private static volatile VoiceUtils singleton = null;
    private boolean isPlaying;
    private MediaPlayer mediaPlayer = null;
    private Context mContext;

    public static final int GET_BALANCE_SUCCESS = 1;
    public static final int RECHARGE_SUCCESS = 2;

    public VoiceUtils(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static VoiceUtils with(Context context) {
        if (singleton == null) {
            synchronized (VoiceUtils.class) {
                if (singleton == null) {
                    singleton = new VoiceUtils(context);
                }
            }
        }
        return singleton;
    }

    public void setIsPlay(boolean isPlaying) {

        this.isPlaying = isPlaying;
    }

    public boolean getIsPlay() {
        return isPlaying;
    }

    public void play(int resId) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return;
        }
        try {
            mediaPlayer = MediaPlayer.create(mContext, resId);
            mediaPlayer.stop();
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(String stramount, int moneyType) {

        String str = null;
        if (GET_BALANCE_SUCCESS == moneyType) {
            str = "$" + PlaySound.capitalValueOf(Double.valueOf(String.format("%.2f", Double.parseDouble(stramount))));
        } else if (RECHARGE_SUCCESS == moneyType) {
            str = "*" + PlaySound.capitalValueOf(Double.valueOf(String.format("%.2f", Double.parseDouble(stramount))));

        }
        System.out.println("金额的长度 " + str);
        String temp = "";

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        playSoundList(1, str, str.length());

    }

    public void playSoundList(final int soundindex, final String soundString, final int soundcount) {
        singleton.setIsPlay(true);
        boolean createState = false;
        if (mediaPlayer != null) {
            mediaPlayer = null;
        }
        System.out.println("加载音频[" + soundindex + "]");
        mediaPlayer = createSound(soundindex, soundString);
        createState = true;

//        if (createState == true)
//            System.out.println("加载音频成功[" + soundindex + "]");
//        else
//            System.out.println("加载音频失败[" + soundindex + "]");

        //播放完成触发此事件
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();//释放音频资源
                int newsoundindex = soundindex;
                System.out.println("释放资源[" + soundindex + "]");
                if (soundindex < soundcount) {
                    newsoundindex = newsoundindex + 1;
                    playSoundList(newsoundindex, soundString, soundcount);
                } else {
                    singleton.setIsPlay(false);
                }

            }
        });
        try {
//            if (createState) {
//                mediaPlayer.prepare();
//            }else {
//                mediaPlayer.prepare();
//            }
            mediaPlayer.prepare();
            mediaPlayer.start();
            System.out.println("播放音频[" + soundindex + "]");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MediaPlayer createSound(int soundIndex, String soundString) {
        MediaPlayer mp = null;
        String soundChar = soundString.substring(soundIndex - 1, soundIndex);
        switch (soundChar) {
            case "零":
                mp = MediaPlayer.create(mContext, R.raw.sound0);
                break;
            case "壹":
                mp = MediaPlayer.create(mContext, R.raw.sound1);
                break;
            case "贰":
                mp = MediaPlayer.create(mContext, R.raw.sound2);
                break;
            case "叁":
                mp = MediaPlayer.create(mContext, R.raw.sound3);
                break;
            case "肆":
                mp = MediaPlayer.create(mContext, R.raw.sound4);
                break;
            case "伍":
                mp = MediaPlayer.create(mContext, R.raw.sound5);
                break;
            case "陆":
                mp = MediaPlayer.create(mContext, R.raw.sound6);
                break;
            case "柒":
                mp = MediaPlayer.create(mContext, R.raw.sound7);
                break;
            case "捌":
                mp = MediaPlayer.create(mContext, R.raw.sound8);
                break;
            case "玖":
                mp = MediaPlayer.create(mContext, R.raw.sound9);
                break;
            case "拾":
                mp = MediaPlayer.create(mContext, R.raw.soundshi);
                break;
            case "佰":
                mp = MediaPlayer.create(mContext, R.raw.soundbai);
                break;
            case "仟":
                mp = MediaPlayer.create(mContext, R.raw.soundqian);
                break;
            case "角":
                mp = MediaPlayer.create(mContext, R.raw.soundjiao);
                break;
            case "分":
                mp = MediaPlayer.create(mContext, R.raw.soundfen);
                break;
            case "元":
                mp = MediaPlayer.create(mContext, R.raw.soundyuan);
                break;
            case "整":
                mp = MediaPlayer.create(mContext, R.raw.soundzheng);
                break;
            case "万":
                mp = MediaPlayer.create(mContext, R.raw.soundwan);
                break;
            case "$":
                mp = MediaPlayer.create(mContext, R.raw.card_balance);
                break;
            case "*":
                mp = MediaPlayer.create(mContext, R.raw.recharge_success_balance);
                break;
            default:
                break;

        }
        mp.stop();
        return mp;
    }
}

