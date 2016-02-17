package com.yydcdut.note.widget.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yuyidong on 15/9/1.
 */
public class CameraGridView extends View {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public CameraGridView(Context context) {
        super(context);
    }

    public CameraGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStrokeWidth(1.5f);
        int deltaHeight = getMeasuredHeight() / 3;
        int deltaWidth = getMeasuredWidth() / 3;
        //画竖线
        canvas.drawLine(deltaWidth, 0, deltaWidth, getMeasuredHeight(), p);
        canvas.drawLine(deltaWidth * 2, 0, deltaWidth * 2, getMeasuredHeight(), p);
        //画横线
        canvas.drawLine(0, deltaHeight, getMeasuredWidth(), deltaHeight, p);
        canvas.drawLine(0, deltaHeight * 2, getMeasuredWidth(), deltaHeight * 2, p);
    }
}
