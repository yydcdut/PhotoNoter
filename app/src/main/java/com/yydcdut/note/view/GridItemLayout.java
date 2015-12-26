package com.yydcdut.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by yuyidong on 15/7/13.
 */
public class GridItemLayout extends FrameLayout {

    public GridItemLayout(Context context) {
        this(context, null);
    }

    public GridItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
    }
}
