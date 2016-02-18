package com.yydcdut.note.widget.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.yydcdut.note.R;


/**
 * Created by yuyidong on 15/8/22.
 */
public class IsoView extends View {
    /* 整个View */
    private int mWidth;
    private int mHeight;
    private int mHalfHeight;
    /* bitmap*/
    private Bitmap mBitmap;
    private int mBitmapWidth;
    private int mBitmapHeight;
    /* 德尔塔 */
    private float mDeltaY = -1;
    /* 笔的宽度 */
    private float mPaintWidth = 2f;
    /* Scroller */
    private Scroller mScroller;
    /* Bitmap的左上角位置 */
    private float mBitmapTopPosition;
    /* 手指在控件外面的话是否还能继续控制 true为能控制，false为不能控制 */
    private boolean mWithoutX = true;

    private float mLastY;

    private Paint mPaint;

    public IsoView(Context context) {
        this(context, null);
    }

    public IsoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IsoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_camera_pg_exp);
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
        mScroller = new Scroller(context);
        mPaint = new Paint();
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setColor(Color.YELLOW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST
                || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(mBitmapWidth, MeasureSpec.getSize(heightMeasureSpec));
            measure(MeasureSpec.makeMeasureSpec(mBitmapWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        } else {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            mHalfHeight = mHeight / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int delta = (int) (mHalfHeight - mDeltaY);
        float fromX = (mWidth - mPaintWidth) / 2;
        canvas.drawLine(fromX, 0, fromX, mHeight / 2 - mBitmapHeight - delta > mHeight - mBitmapHeight * 3 / 2 ?
                mHeight - mBitmapHeight * 3 / 2 : mHeight / 2 - mBitmapHeight - delta < 0 ?
                0 : mHeight / 2 - mBitmapHeight - delta, mPaint);
        canvas.drawBitmap(mBitmap, 0, (mHeight - mBitmapHeight) / 2 - delta < 0 ?
                0 : (mHeight - mBitmapHeight) / 2 - delta > mHeight - mBitmapHeight ?
                mHeight - mBitmapHeight : (mHeight - mBitmapHeight) / 2 - delta, null);
        canvas.drawLine(fromX, mHeight / 2 + mBitmapHeight - delta < mBitmapHeight * 3 / 2 ?
                mBitmapHeight * 3 / 2 : mHeight / 2 + mBitmapHeight - delta > mHeight ?
                mHeight : mHeight / 2 + mBitmapHeight - delta, fromX, mHeight, mPaint);

        mBitmapTopPosition = (mHeight - mBitmapHeight) / 2 - delta < 0 ?
                0 : (mHeight - mBitmapHeight) / 2 - delta > mHeight - mBitmapHeight ?
                mHeight - mBitmapHeight : (mHeight - mBitmapHeight) / 2 - delta;

        float value = (mBitmapTopPosition) * mValueMax / (mHeight - mBitmapHeight);
        if (mOnValueChangedListener != null) {
            mOnValueChangedListener.onValueChanged(this, (int) value);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (y > mBitmapTopPosition && y < mBitmapTopPosition + mBitmapHeight) {
                    return true;
                } else {
                    mLastY = mDeltaY;
                    mScroller.startScroll(0, 0, 0, (int) (y - mDeltaY), 1000);
                    postInvalidate();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mWithoutX ? true : (x > 0 && x < mBitmapWidth)) {
                    mDeltaY = y;
                    invalidate();
                }
                break;
        }
        if (mOnIsoViewOnTouchedListener != null) {
            mOnIsoViewOnTouchedListener.onTouched();
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            mDeltaY = mLastY + mScroller.getCurrY();
            invalidate();
            postInvalidate();
        }
    }

    private OnValueChangedListener mOnValueChangedListener;

    public interface OnValueChangedListener {
        void onValueChanged(View view, int value);
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        mOnValueChangedListener = onValueChangedListener;
    }

    private int mValueMax = 100;

    public void setValueMax(int valueMax) {
        mValueMax = valueMax;
    }

    public int getValueMax() {
        return mValueMax;
    }

    public void setValue(int value) {
        float percent = ((float) (value)) / mValueMax;
        mDeltaY = mHeight * percent;
        invalidate();
    }

    private OnIsoViewOnTouchedListener mOnIsoViewOnTouchedListener;

    public void setOnIsoViewOnTouchedListener(OnIsoViewOnTouchedListener onIsoViewOnTouchedListener) {
        mOnIsoViewOnTouchedListener = onIsoViewOnTouchedListener;
    }

    public interface OnIsoViewOnTouchedListener {
        void onTouched();
    }
}
