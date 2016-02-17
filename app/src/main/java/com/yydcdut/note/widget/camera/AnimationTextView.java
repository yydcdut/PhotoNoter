package com.yydcdut.note.widget.camera;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

/**
 * Created by yuyidong on 14-12-23.
 */
public class AnimationTextView extends TextView implements Handler.Callback {
    private static final int MSG_ADD = 1;
    private OnAnimationTextViewListener mListener;
    private Animation mAnimation;
    private int mTime;
    private Handler mHandler;

    private boolean mIsCountDown = false;

    public AnimationTextView(Context context) {
        this(context, null);
    }

    public AnimationTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimation();
        mHandler = new Handler(this);
    }

    public void setOnAnimationTextViewListener(OnAnimationTextViewListener listener) {
        mListener = listener;
    }

    private void initAnimation() {
        mAnimation = new ScaleAnimation(3.0f, 1.0f, 3.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(1000);
    }


    public void start(int num) {
        if (mIsCountDown) {
            return;
        }
        mTime = num;
        this.setVisibility(VISIBLE);
        mIsCountDown = true;
        setText((mTime) + "");
        startAnimation(mAnimation);
        mHandler.sendEmptyMessageDelayed(MSG_ADD, 1000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ADD:
                mTime--;
                if (mTime > 0) {
                    setText((mTime) + "");
                    startAnimation(mAnimation);
                    mHandler.sendEmptyMessageDelayed(MSG_ADD, 1000);
                } else {
                    mIsCountDown = false;
                    setVisibility(GONE);
                    if (mListener != null) {
                        mListener.onTextDisappear();
                    }
                }
                break;
        }
        return false;
    }

    /**
     * 是否在计时
     *
     * @return
     */
    public boolean isCountDown() {
        return mIsCountDown;
    }

    /**
     * 是否要打断
     */
    public void interrupt() {
        mIsCountDown = false;
        mHandler.removeMessages(MSG_ADD);
        setVisibility(GONE);
        if (mListener != null) {
            mListener.onTextCancel();
        }
    }

    public interface OnAnimationTextViewListener {

        void onTextCancel();

        void onTextDisappear();
    }

}
