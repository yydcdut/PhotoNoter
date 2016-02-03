package com.yydcdut.note.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.yydcdut.note.R;
import com.yydcdut.note.utils.Utils;

/**
 * Created by yuyidong on 15/10/23.
 * todo 这个UI要改
 */
public class UserCenterArrowView extends View {
    private float mMarginWidth = 0;
    private float mWidth;
    private float mHeight;
    private int mColor = Color.RED;
    private int mScreenWidth;

    public UserCenterArrowView(Context context) {
        this(context, null);
    }

    public UserCenterArrowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserCenterArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWidth = context.getResources().getDimension(R.dimen.dimen_24dip);
        mScreenWidth = Utils.sScreenWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = (float) getMeasuredHeight();
    }

    public void setColorAndMarginWidth(int color, int marginWidth) {
        mColor = color;
        mMarginWidth = marginWidth;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);

        float deltaX = mWidth / 180;
        for (int i = 0; i < 180; i++) {
            double point = Math.sin((i * Math.PI) / 180);
            canvas.drawLine((mScreenWidth / 2 - mWidth / 2) + i * deltaX + mMarginWidth,
                    (float) (1 - point) * mHeight,
                    (mScreenWidth / 2 - mWidth / 2) + (i + 1) * deltaX + mMarginWidth,
                    mHeight,
                    paint);
        }
    }
}
