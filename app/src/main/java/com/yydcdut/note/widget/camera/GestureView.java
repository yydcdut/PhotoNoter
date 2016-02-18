package com.yydcdut.note.widget.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by yuyidong on 15/8/28.
 */
public class GestureView extends View {
    /* Gesture */
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    public GestureView(Context context) {
        this(context, null);
    }

    public GestureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleGestureDetector = new ScaleGestureDetector(context, mOnScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mViewWidth > 0 && mViewHeight > 0) {
            setMeasuredDimension(mViewWidth, mViewHeight);
        }
    }

    private int mViewWidth;
    private int mViewHeight;

    public void init(int viewWidth, int viewHeight) {
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
        measure(MeasureSpec.makeMeasureSpec(mViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mViewHeight, MeasureSpec.EXACTLY));
        requestLayout();
    }

    private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (mOnZoomScaleListener != null) {
                return mOnZoomScaleListener.onZoomChange(detector.getCurrentSpan());
            } else {
                return true;
            }

        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (mOnZoomScaleListener != null) {
                return mOnZoomScaleListener.onZoomBegin(detector.getCurrentSpan());
            } else {
                return true;
            }
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };

    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mOnFocusListener != null) {
                return mOnFocusListener.onFocusTrigger(e.getX(), e.getY());
            } else {
                return true;
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnFocusListener != null) {
            mOnFocusListener.getMotionEvent(event);
        }
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal;
    }

    private OnZoomScaleListener mOnZoomScaleListener;

    public void setOnZoomScaleListener(OnZoomScaleListener onZoomScaleListener) {
        mOnZoomScaleListener = onZoomScaleListener;
    }

    public interface OnZoomScaleListener {
        boolean onZoomChange(float num);

        boolean onZoomBegin(float currentSpan);
    }

    private OnFocusListener mOnFocusListener;

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        mOnFocusListener = onFocusListener;
    }

    public interface OnFocusListener {
        void getMotionEvent(MotionEvent event);

        boolean onFocusTrigger(float x, float y);
    }
}
