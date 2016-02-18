package com.yydcdut.note.widget.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yydcdut.note.R;

/**
 * Created by yuyidong on 15/8/27.
 */
public class FocusView extends View {
    /*
     * Action state
     */
    private int mActionState;
    private static final int STATE_NOTHING = -1;
    private static final int STATE_2_FINGERS = -2;
    /*
     * Handler的一些东西
     */
    private Handler mHandler;
    public static final int IMAGE_DISAPPEAR = 0;
    public static final int IMAGE_DISAPPEAR_GOOD = 1;
    /**
     * 前置摄像头不支持聚焦，所以这个标志位来判断
     */
    private boolean mIsSupport = false;
    /**
     * 一旦Move之后，手指速度快了的话就跟不上move了，所以用这个标志位
     */
    private boolean mMoveFollow = false;
    /**
     * 最大宽度
     */
    private final float MAX_LENGTH = this.getContext().getResources().getDimension(R.dimen.focus_length_max);
    /**
     * 当前宽度
     */
    private float mLength = this.getContext().getResources().getDimension(R.dimen.focus_length_min);
    /*
     * 坐标
     */
    private float mX;
    private float mY;
    /*
     * 画笔
     */
    private Paint mPaint;

    private int mViewWidth;
    private int mViewHeight;

    public FocusView(Context context) {
        this(context, null);
    }

    public FocusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLooper();
        mPaint = new Paint();
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    private void initLooper() {
        mHandler = new Handler(Looper.getMainLooper(), mCallback);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mViewWidth > 0 && mViewHeight > 0) {
            setMeasuredDimension(mViewWidth, mViewHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mIsSupport) {
            return;
        }
        super.onDraw(canvas);

        canvas.drawArc(calculateRectF(), 0, 360, false, mPaint);
        if (mActionState == MotionEvent.ACTION_UP && mLength <= MAX_LENGTH) {
            mLength += 5;
            if (mLength >= MAX_LENGTH) {
                mLength = MAX_LENGTH;
            }
            invalidate();
        } else if (mActionState == MotionEvent.ACTION_MOVE) {
            mLength -= 2;
            if (mLength <= getResources().getDimension(R.dimen.focus_length_min)) {
                mLength = getResources().getDimension(R.dimen.focus_length_min);
            }
        }
    }

    /**
     * 计算位置
     *
     * @return
     */
    private RectF calculateRectF() {
        return new RectF(mX - mLength / 2, mY - mLength / 2, mX + mLength / 2, mY + mLength / 2);
    }

    /**
     * 聚焦成功
     */
    private void focusSuccess() {
        if (!mIsSupport) {
            return;
        }
        this.setVisibility(VISIBLE);
        mPaint.setColor(Color.YELLOW);
        invalidate();
        mHandler.sendEmptyMessageDelayed(IMAGE_DISAPPEAR_GOOD, 400);
    }

    /**
     * 停止聚焦
     */
    private void stopFocus() {
        if (!mIsSupport) {
            return;
        }
        this.setVisibility(GONE);
    }

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case IMAGE_DISAPPEAR:
                    focusSuccess();
                    break;
                case IMAGE_DISAPPEAR_GOOD:
                    stopFocus();
                    if (mOnFocusStateChangedListener != null) {
                        mOnFocusStateChangedListener.onFocusDisappeared();
                    }
                    break;
            }
            return false;
        }
    };

    public void setNotSupport() {
        mIsSupport = false;
    }

    /**
     * 开始聚焦动画
     *
     * @param x
     * @param y
     * @return
     */
    public boolean startFocusing(float x, float y) {
        if (mHandler.hasMessages(IMAGE_DISAPPEAR) || mHandler.hasMessages(IMAGE_DISAPPEAR_GOOD)) {
            return false;
        }
        mPaint.setColor(Color.YELLOW);
        this.setVisibility(VISIBLE);
        mLength = getContext().getResources().getDimension(R.dimen.focus_length_min);
        mActionState = MotionEvent.ACTION_UP;
        mX = x;
        mY = y;
        invalidate();
        mHandler.sendEmptyMessageDelayed(IMAGE_DISAPPEAR, 1000);
        if (mOnFocusStateChangedListener != null) {
            mOnFocusStateChangedListener.onBeginFocusing(x, y);
        }
        return true;
    }

    public void initFocus(int viewWidth, int viewHeight) {
        mIsSupport = true;
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
        this.setVisibility(INVISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopFocus();
            }
        }, 100);
        measure(MeasureSpec.makeMeasureSpec(mViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mViewHeight, MeasureSpec.EXACTLY));
        requestLayout();
    }

    public void setMotionEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    if ((event.getX() > mX - mLength / 2 && event.getX() < mX + mLength / 2 &&
                            event.getY() > mY - mLength / 2 && event.getY() < mY + mLength / 2)) {
                        mActionState = MotionEvent.ACTION_MOVE;
                        mHandler.removeMessages(IMAGE_DISAPPEAR);
                        mHandler.removeMessages(IMAGE_DISAPPEAR_GOOD);
                        mX = event.getX();
                        mY = event.getY();
                        invalidate();
                        mMoveFollow = true;
                        if (mOnFocusStateChangedListener != null) {
                            mOnFocusStateChangedListener.onBeginMoving();
                        }
                    } else if (mMoveFollow) {
                        mX = event.getX();
                        mY = event.getY();
                        invalidate();
                    } else {
                        mActionState = STATE_NOTHING;
                        mMoveFollow = false;
                    }
                } else {
                    mActionState = STATE_2_FINGERS;
                    mMoveFollow = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (event.getPointerCount() == 1 && !mHandler.hasMessages(IMAGE_DISAPPEAR) &&
                        !mHandler.hasMessages(IMAGE_DISAPPEAR_GOOD) && mActionState != STATE_2_FINGERS) {
                    if (mOnTriggerFocusListener != null) {
                        mOnTriggerFocusListener.onTriggerFocus(event.getX(), event.getY());
                    }
                    mX = event.getX();
                    mY = event.getY();
                    invalidate();
                } else {
                    mActionState = STATE_NOTHING;
                }
                mMoveFollow = false;
                break;
        }
    }

    public void delayDisappear() {
        if (!mIsSupport) {
            return;
        }
        if (mHandler.hasMessages(IMAGE_DISAPPEAR_GOOD)) {
            mHandler.removeMessages(IMAGE_DISAPPEAR_GOOD);
            mHandler.sendEmptyMessageDelayed(IMAGE_DISAPPEAR_GOOD, 400);
        }
        if (mHandler.hasMessages(IMAGE_DISAPPEAR)) {
            mHandler.removeMessages(IMAGE_DISAPPEAR);
            mHandler.sendEmptyMessageDelayed(IMAGE_DISAPPEAR, 400);
        }
    }

    private OnTriggerFocusListener mOnTriggerFocusListener;

    public void setOnTriggerFocusListener(OnTriggerFocusListener onTriggerFocusListener) {
        mOnTriggerFocusListener = onTriggerFocusListener;
    }

    public interface OnTriggerFocusListener {
        void onTriggerFocus(float x, float y);
    }

    private OnFocusStateChangedListener mOnFocusStateChangedListener;

    public void setOnFocusStateChangedListener(OnFocusStateChangedListener onFocusStateChangedListener) {
        mOnFocusStateChangedListener = onFocusStateChangedListener;
    }

    public interface OnFocusStateChangedListener {
        void onBeginFocusing(float x, float y);

        void onBeginMoving();

        void onFocusDisappeared();
    }

}
