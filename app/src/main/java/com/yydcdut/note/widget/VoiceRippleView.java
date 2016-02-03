package com.yydcdut.note.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15/11/11.
 */
public class VoiceRippleView extends ImageView implements Runnable, Handler.Callback {
    private static final int STATE_ADD = 1;
    private static final int STATE_MINUS = 0;
    private int mState = STATE_MINUS;

    private boolean mWannaFinish = false;
    private boolean mWannaPause = false;
    private Thread mRunnaleThread;
    private Handler mHandler;

    private static final float VOICE_DELTA = 0.05f;
    private float mRealVoice = 0.2f;
    private float mVoice = 0.2f;

    /**
     * 0.0 ~~ 1.0
     */
    private byte[] mObject;

    public VoiceRippleView(Context context) {
        this(context, null);
    }

    public VoiceRippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceRippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mObject = new byte[1];
        setScaleX(0.0f);
        setScaleY(0.0f);
    }

    public void startAnimation() {
        if (mRunnaleThread == null) {
            mHandler = new Handler(this);
            mRunnaleThread = new Thread(this);
            mRunnaleThread.start();
        } else {
            mWannaPause = false;
            synchronized (mObject) {
                mObject.notifyAll();
            }
        }
    }

    public void pauseAnimation() {
        mWannaPause = true;
    }

    public void stopAnimation() {
        mWannaFinish = true;
        mRunnaleThread = null;
        mHandler = null;
        mObject = null;
    }

    public void setVoice(float voice) {
        mRealVoice = voice;
    }

    @Override
    public void run() {
        while (!mWannaFinish) {
            if (mWannaPause) {
                synchronized (mObject) {
                    try {
                        mObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (mVoice < mRealVoice - VOICE_DELTA * 2) {
                    //mVoice不断的加
                    mVoice += VOICE_DELTA;
                    sleep(25);
                } else if (mVoice > mRealVoice + VOICE_DELTA * 2) {
                    //mVoice不断的减
                    mVoice -= VOICE_DELTA;
                    sleep(25);
                } else {
                    //在-0.05~~0.05的范围不断加减
                    switch (mState) {
                        case STATE_ADD:
                            mVoice += VOICE_DELTA / 2;
                            if (mVoice > mRealVoice + VOICE_DELTA * 2) {
                                mState = STATE_MINUS;
                            }
                            break;
                        case STATE_MINUS:
                            mVoice -= VOICE_DELTA / 2;
                            if (mVoice < mRealVoice - VOICE_DELTA * 2) {
                                mState = STATE_ADD;
                            }
                            break;
                    }
                    sleep(50);
                }
                float value = mVoice * 0.4f + 0.65f;
                Message msg = new Message();
                msg.obj = value;
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        float value = (float) msg.obj;
        this.setScaleX(value);
        this.setScaleY(value);
        return false;
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
