package com.yydcdut.note.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuyidong on 15/12/27.
 */
public class GridItemImageView extends ImageView {
    private int mSize = 0;

    public GridItemImageView(Context context) {
        this(context, null);
    }

    public GridItemImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridItemImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSize(int size) {
        mSize = size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize, mSize);
    }
}
